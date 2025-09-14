package com.lunar.demo.controller;

import com.lunar.demo.dto.PaymentCreateRequest;
import com.lunar.demo.dto.PaymentResponse;
import com.lunar.demo.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentCreateRequest request) {
        log.info("Payment creation attempt for booking: {}", request.getBookingId());
        PaymentResponse payment = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }
    
    @PostMapping("/verify")
    public ResponseEntity<PaymentResponse> verifyPayment(@RequestParam String paymentId,
                                                       @RequestParam String razorpayOrderId,
                                                       @RequestParam String razorpaySignature) {
        log.info("Payment verification attempt for payment ID: {}", paymentId);
        PaymentResponse payment = paymentService.verifyPayment(paymentId, razorpayOrderId, razorpaySignature);
        return ResponseEntity.ok(payment);
    }
    
    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponse> processRefund(@PathVariable Long id,
                                                       @RequestParam BigDecimal refundAmount,
                                                       @RequestParam String reason) {
        log.info("Refund processing attempt for payment ID: {}", id);
        PaymentResponse payment = paymentService.processRefund(id, refundAmount, reason);
        return ResponseEntity.ok(payment);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        log.info("Get payment by ID: {}", id);
        PaymentResponse payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }
    
    @GetMapping
    public ResponseEntity<Page<PaymentResponse>> getUserPayments(Pageable pageable) {
        log.info("Get user payments request");
        Page<PaymentResponse> payments = paymentService.getUserPayments(pageable);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PaymentResponse>> getBookingPayments(@PathVariable Long bookingId) {
        log.info("Get payments for booking: {}", bookingId);
        List<PaymentResponse> payments = paymentService.getBookingPayments(bookingId);
        return ResponseEntity.ok(payments);
    }
    
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestBody String payload,
                                            @RequestHeader("X-Razorpay-Signature") String signature) {
        log.info("Razorpay webhook received");
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }
}
