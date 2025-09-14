package com.lunar.demo.service;

import com.lunar.demo.dto.PaymentCreateRequest;
import com.lunar.demo.dto.PaymentResponse;
import com.lunar.demo.entity.Booking;
import com.lunar.demo.entity.Payment;
import com.lunar.demo.entity.User;
import com.lunar.demo.repository.BookingRepository;
import com.lunar.demo.repository.PaymentRepository;
import com.lunar.demo.repository.UserRepository;
import com.lunar.demo.security.UserPrincipal;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    @Value("${razorpay.key-id}")
    private String razorpayKeyId;
    
    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;
    
    @Value("${razorpay.webhook-secret}")
    private String razorpayWebhookSecret;
    
    @Transactional
    public PaymentResponse createPayment(PaymentCreateRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new RuntimeException("Booking is not in pending status");
        }
        
        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", booking.getNetAmount().multiply(new BigDecimal("100")).intValue()); // Convert to paise
            orderRequest.put("currency", booking.getCurrency());
            orderRequest.put("receipt", booking.getBookingReference());
            orderRequest.put("notes", new JSONObject().put("booking_id", booking.getId()));
            
            Order order = razorpay.orders.create(orderRequest);
            
            Payment payment = Payment.builder()
                    .paymentReference(generatePaymentReference())
                    .externalPaymentId(order.get("id"))
                    .paymentMethod(Payment.PaymentMethod.RAZORPAY)
                    .status(Payment.PaymentStatus.PENDING)
                    .amount(booking.getNetAmount())
                    .currency(booking.getCurrency())
                    .processingFee(booking.getServiceFee())
                    .netAmount(booking.getNetAmount().subtract(booking.getServiceFee()))
                    .paymentGateway("razorpay")
                    .gatewayTransactionId(order.get("id"))
                    .gatewayResponse(order.toString())
                    .booking(booking)
                    .user(user)
                    .build();
            
            payment = paymentRepository.save(payment);
            
            log.info("Payment created successfully with ID: {} and Razorpay order ID: {}", 
                    payment.getId(), order.get("id"));
            
            return mapToPaymentResponse(payment, order);
            
        } catch (RazorpayException e) {
            log.error("Error creating Razorpay payment for booking: {}", request.getBookingId(), e);
            throw new RuntimeException("Failed to create payment: " + e.getMessage());
        }
    }
    
    @Transactional
    public PaymentResponse verifyPayment(String paymentId, String razorpayOrderId, String razorpaySignature) {
        Payment payment = paymentRepository.findByExternalPaymentId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            
            // Verify signature
            String generatedSignature = generateSignature(razorpayOrderId + "|" + paymentId, razorpayWebhookSecret);
            
            if (!generatedSignature.equals(razorpaySignature)) {
                payment.markAsFailed("Invalid signature");
                paymentRepository.save(payment);
                throw new RuntimeException("Invalid payment signature");
            }
            
            // Verify payment with Razorpay
            com.razorpay.Payment razorpayPayment = razorpay.payments.fetch(paymentId);
            
            if ("captured".equals(razorpayPayment.get("status"))) {
                payment.markAsCompleted();
                payment.setGatewayTransactionId(paymentId);
                payment.setGatewayResponse(razorpayPayment.toString());
                payment = paymentRepository.save(payment);
                
                // Confirm booking
                Booking booking = payment.getBooking();
                booking.setStatus(Booking.BookingStatus.CONFIRMED);
                bookingRepository.save(booking);
                
                // Send confirmation email
                emailService.sendBookingConfirmationEmail(booking);
                
                log.info("Payment verified and completed for payment ID: {}", payment.getId());
                
                return mapToPaymentResponse(payment, null);
            } else {
                payment.markAsFailed("Payment not captured");
                paymentRepository.save(payment);
                throw new RuntimeException("Payment not captured");
            }
            
        } catch (RazorpayException e) {
            log.error("Error verifying payment: {}", paymentId, e);
            payment.markAsFailed("Verification failed: " + e.getMessage());
            paymentRepository.save(payment);
            throw new RuntimeException("Failed to verify payment: " + e.getMessage());
        }
    }
    
    @Transactional
    public PaymentResponse processRefund(Long paymentId, BigDecimal refundAmount, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (!payment.isCompleted()) {
            throw new RuntimeException("Payment is not completed");
        }
        
        if (!payment.canBeRefunded()) {
            throw new RuntimeException("Payment cannot be refunded");
        }
        
        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", refundAmount.multiply(new BigDecimal("100")).intValue());
            refundRequest.put("notes", new JSONObject().put("reason", reason));
            
            com.razorpay.Refund refund = razorpay.payments.refund(payment.getGatewayTransactionId(), refundRequest);
            
            payment.processRefund(refundAmount, reason);
            payment.setGatewayResponse(refund.toString());
            payment = paymentRepository.save(payment);
            
            // Update booking status if fully refunded
            if (payment.getStatus() == Payment.PaymentStatus.REFUNDED) {
                Booking booking = payment.getBooking();
                booking.setStatus(Booking.BookingStatus.REFUNDED);
                bookingRepository.save(booking);
            }
            
            log.info("Refund processed for payment ID: {} with amount: {}", paymentId, refundAmount);
            
            return mapToPaymentResponse(payment, null);
            
        } catch (RazorpayException e) {
            log.error("Error processing refund for payment: {}", paymentId, e);
            throw new RuntimeException("Failed to process refund: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        return mapToPaymentResponse(payment, null);
    }
    
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getUserPayments(Pageable pageable) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<Payment> payments = paymentRepository.findByUserId(userPrincipal.getId(), pageable);
        return payments.map(payment -> mapToPaymentResponse(payment, null));
    }
    
    @Transactional(readOnly = true)
    public List<PaymentResponse> getBookingPayments(Long bookingId) {
        List<Payment> payments = paymentRepository.findByBookingId(bookingId);
        return payments.stream()
                .map(payment -> mapToPaymentResponse(payment, null))
                .collect(Collectors.toList());
    }
    
    public void handleWebhook(String payload, String signature) {
        try {
            // Verify webhook signature
            String generatedSignature = generateSignature(payload, razorpayWebhookSecret);
            
            if (!generatedSignature.equals(signature)) {
                log.error("Invalid webhook signature");
                return;
            }
            
            JSONObject webhookData = new JSONObject(payload);
            String event = webhookData.getString("event");
            
            if ("payment.captured".equals(event)) {
                handlePaymentCaptured(webhookData);
            } else if ("payment.failed".equals(event)) {
                handlePaymentFailed(webhookData);
            }
            
        } catch (Exception e) {
            log.error("Error processing webhook", e);
        }
    }
    
    private void handlePaymentCaptured(JSONObject webhookData) {
        JSONObject paymentData = webhookData.getJSONObject("payload").getJSONObject("payment");
        String razorpayOrderId = paymentData.getString("order_id");
        
        Payment payment = paymentRepository.findByExternalPaymentId(razorpayOrderId)
                .orElse(null);
        
        if (payment != null && payment.getStatus() == Payment.PaymentStatus.PENDING) {
            payment.markAsCompleted();
            payment.setGatewayTransactionId(paymentData.getString("id"));
            payment.setWebhookReceivedAt(LocalDateTime.now());
            payment.setWebhookData(webhookData.toString());
            paymentRepository.save(payment);
            
            // Confirm booking
            Booking booking = payment.getBooking();
            booking.setStatus(Booking.BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
            
            // Send confirmation email
            emailService.sendBookingConfirmationEmail(booking);
            
            log.info("Payment captured via webhook for payment ID: {}", payment.getId());
        }
    }
    
    private void handlePaymentFailed(JSONObject webhookData) {
        JSONObject paymentData = webhookData.getJSONObject("payload").getJSONObject("payment");
        String razorpayOrderId = paymentData.getString("order_id");
        
        Payment payment = paymentRepository.findByExternalPaymentId(razorpayOrderId)
                .orElse(null);
        
        if (payment != null && payment.getStatus() == Payment.PaymentStatus.PENDING) {
            payment.markAsFailed("Payment failed via webhook");
            payment.setWebhookReceivedAt(LocalDateTime.now());
            payment.setWebhookData(webhookData.toString());
            paymentRepository.save(payment);
            
            log.info("Payment failed via webhook for payment ID: {}", payment.getId());
        }
    }
    
    private String generatePaymentReference() {
        return "PAY-" + System.currentTimeMillis() + "-" + 
               String.format("%06d", (int) (Math.random() * 1000000));
    }
    
    private String generateSignature(String payload, String secret) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(secret.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes());
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    private PaymentResponse mapToPaymentResponse(Payment payment, Order order) {
        PaymentResponse.PaymentResponseBuilder builder = PaymentResponse.builder()
                .id(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .externalPaymentId(payment.getExternalPaymentId())
                .paymentMethod(payment.getPaymentMethod().name())
                .status(payment.getStatus().name())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .processingFee(payment.getProcessingFee())
                .netAmount(payment.getNetAmount())
                .paymentGateway(payment.getPaymentGateway())
                .gatewayTransactionId(payment.getGatewayTransactionId())
                .failureReason(payment.getFailureReason())
                .processedAt(payment.getProcessedAt())
                .refundedAt(payment.getRefundedAt())
                .refundAmount(payment.getRefundAmount())
                .refundReason(payment.getRefundReason())
                .bookingId(payment.getBooking().getId())
                .userId(payment.getUser().getId())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt());
        
        if (order != null) {
            builder.razorpayOrderId(order.get("id"))
                   .razorpayKeyId(razorpayKeyId);
        }
        
        return builder.build();
    }
}
