package com.lunar.demo.service;

import com.lunar.demo.entity.Booking;
import com.lunar.demo.entity.Event;
import com.lunar.demo.entity.Ticket;
import com.lunar.demo.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final QrCodeService qrCodeService;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    @Transactional
    public void sendBookingConfirmationEmail(Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("Booking Confirmation - " + booking.getEvent().getTitle());
            
            String htmlContent = generateBookingConfirmationHtml(booking);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Booking confirmation email sent to: {}", booking.getUser().getEmail());
            
        } catch (MessagingException e) {
            log.error("Error sending booking confirmation email", e);
        }
    }
    
    @Transactional
    public void sendTicketEmail(Booking booking, Ticket ticket) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("Your Ticket - " + booking.getEvent().getTitle());
            
            String htmlContent = generateTicketHtml(booking, ticket);
            helper.setText(htmlContent, true);
            
            // Add QR code as attachment
            String qrCodeBase64 = qrCodeService.generateQrCodeForTicket(
                    booking.getId(), ticket.getId(), ticket.getTicketCode());
            
            mailSender.send(message);
            log.info("Ticket email sent to: {}", booking.getUser().getEmail());
            
        } catch (MessagingException e) {
            log.error("Error sending ticket email", e);
        }
    }
    
    @Transactional
    public void sendBookingCancellationEmail(Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("Booking Cancelled - " + booking.getEvent().getTitle());
            
            String htmlContent = generateBookingCancellationHtml(booking);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Booking cancellation email sent to: {}", booking.getUser().getEmail());
            
        } catch (MessagingException e) {
            log.error("Error sending booking cancellation email", e);
        }
    }
    
    @Transactional
    public void sendEventReminderEmail(Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("Event Reminder - " + booking.getEvent().getTitle());
            
            String htmlContent = generateEventReminderHtml(booking);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Event reminder email sent to: {}", booking.getUser().getEmail());
            
        } catch (MessagingException e) {
            log.error("Error sending event reminder email", e);
        }
    }
    
    private String generateBookingConfirmationHtml(Booking booking) {
        Event event = booking.getEvent();
        User user = booking.getUser();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' h:mm a");
        
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Booking Confirmation</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .booking-details { background: white; padding: 20px; border-radius: 8px; margin: 20px 0; }
                    .event-details { background: white; padding: 20px; border-radius: 8px; margin: 20px 0; }
                    .ticket-info { background: #e8f4fd; padding: 15px; border-radius: 8px; margin: 15px 0; }
                    .qr-code { text-align: center; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 30px; color: #666; }
                    .button { display: inline-block; background: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 10px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Booking Confirmed!</h1>
                        <p>Thank you for your booking with Lunar Events</p>
                    </div>
                    
                    <div class="content">
                        <div class="booking-details">
                            <h2>Booking Details</h2>
                            <p><strong>Booking Reference:</strong> %s</p>
                            <p><strong>Total Amount:</strong> ₹%.2f</p>
                            <p><strong>Booking Date:</strong> %s</p>
                            <p><strong>Status:</strong> Confirmed</p>
                        </div>
                        
                        <div class="event-details">
                            <h2>Event Details</h2>
                            <h3>%s</h3>
                            <p><strong>Date & Time:</strong> %s</p>
                            <p><strong>Venue:</strong> %s</p>
                            <p><strong>Address:</strong> %s</p>
                            <p><strong>Organizer:</strong> %s</p>
                        </div>
                        
                        <div class="ticket-info">
                            <h3>Your Tickets</h3>
                            <p><strong>Number of Tickets:</strong> %d</p>
                            <p>Please bring a valid ID and this confirmation email to the event.</p>
                        </div>
                        
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s/bookings/%d" class="button">View Booking Details</a>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>This is an automated email. Please do not reply.</p>
                        <p>© 2024 Lunar Events. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            booking.getBookingReference(),
            booking.getNetAmount().doubleValue(),
            booking.getCreatedAt().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' h:mm a")),
            event.getTitle(),
            event.getStartDate().format(formatter),
            event.getVenueName(),
            event.getVenueAddress(),
            event.getOrganizer().getFullName(),
            booking.getTotalTickets(),
            baseUrl,
            booking.getId()
        );
    }
    
    private String generateTicketHtml(Booking booking, Ticket ticket) {
        Event event = booking.getEvent();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' h:mm a");
        
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Your Event Ticket</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .ticket { background: white; padding: 30px; border-radius: 8px; margin: 20px 0; border: 2px solid #667eea; }
                    .qr-code { text-align: center; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 30px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎫 Your Event Ticket</h1>
                        <p>Present this ticket at the event entrance</p>
                    </div>
                    
                    <div class="content">
                        <div class="ticket">
                            <h2>%s</h2>
                            <p><strong>Date & Time:</strong> %s</p>
                            <p><strong>Venue:</strong> %s</p>
                            <p><strong>Ticket Code:</strong> %s</p>
                            <p><strong>Seat:</strong> %s</p>
                            
                            <div class="qr-code">
                                <p><strong>QR Code:</strong></p>
                                <img src="data:image/png;base64,%s" alt="QR Code" style="max-width: 200px;">
                            </div>
                        </div>
                        
                        <div style="background: #fff3cd; padding: 15px; border-radius: 8px; margin: 20px 0;">
                            <h4>Important Instructions:</h4>
                            <ul>
                                <li>Please arrive 15 minutes before the event starts</li>
                                <li>Bring a valid photo ID</li>
                                <li>Keep this ticket safe - it cannot be replaced</li>
                                <li>Show this ticket (digital or printed) at the entrance</li>
                            </ul>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>This is an automated email. Please do not reply.</p>
                        <p>© 2024 Lunar Events. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            event.getTitle(),
            event.getStartDate().format(formatter),
            event.getVenueName(),
            ticket.getTicketCode(),
            ticket.getSeatNumber() != null ? ticket.getSeatNumber() : "General Admission",
            qrCodeService.generateQrCodeForTicket(booking.getId(), ticket.getId(), ticket.getTicketCode())
        );
    }
    
    private String generateBookingCancellationHtml(Booking booking) {
        Event event = booking.getEvent();
        
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Booking Cancelled</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #dc3545; color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .booking-details { background: white; padding: 20px; border-radius: 8px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 30px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>❌ Booking Cancelled</h1>
                        <p>Your booking has been cancelled</p>
                    </div>
                    
                    <div class="content">
                        <div class="booking-details">
                            <h2>Booking Details</h2>
                            <p><strong>Booking Reference:</strong> %s</p>
                            <p><strong>Event:</strong> %s</p>
                            <p><strong>Cancellation Reason:</strong> %s</p>
                            <p><strong>Cancelled On:</strong> %s</p>
                        </div>
                        
                        <div style="background: #d1ecf1; padding: 15px; border-radius: 8px; margin: 20px 0;">
                            <h4>Refund Information:</h4>
                            <p>If you are eligible for a refund, it will be processed within 5-7 business days to your original payment method.</p>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>This is an automated email. Please do not reply.</p>
                        <p>© 2024 Lunar Events. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            booking.getBookingReference(),
            event.getTitle(),
            booking.getCancellationReason(),
            booking.getCancelledAt().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' h:mm a"))
        );
    }
    
    private String generateEventReminderHtml(Booking booking) {
        Event event = booking.getEvent();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' h:mm a");
        
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Event Reminder</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #28a745 0%%, #20c997 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .event-details { background: white; padding: 20px; border-radius: 8px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 30px; color: #666; }
                    .button { display: inline-block; background: #28a745; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 10px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>⏰ Event Reminder</h1>
                        <p>Your event is coming up soon!</p>
                    </div>
                    
                    <div class="content">
                        <div class="event-details">
                            <h2>%s</h2>
                            <p><strong>Date & Time:</strong> %s</p>
                            <p><strong>Venue:</strong> %s</p>
                            <p><strong>Address:</strong> %s</p>
                            <p><strong>Booking Reference:</strong> %s</p>
                        </div>
                        
                        <div style="background: #d4edda; padding: 15px; border-radius: 8px; margin: 20px 0;">
                            <h4>Don't forget to bring:</h4>
                            <ul>
                                <li>Your ticket (digital or printed)</li>
                                <li>A valid photo ID</li>
                                <li>Any special requirements mentioned in your booking</li>
                            </ul>
                        </div>
                        
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s/bookings/%d" class="button">View Your Booking</a>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>This is an automated email. Please do not reply.</p>
                        <p>© 2024 Lunar Events. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            event.getTitle(),
            event.getStartDate().format(formatter),
            event.getVenueName(),
            event.getVenueAddress(),
            booking.getBookingReference(),
            baseUrl,
            booking.getId()
        );
    }
}
