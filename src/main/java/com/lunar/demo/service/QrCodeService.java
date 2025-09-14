package com.lunar.demo.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
@Slf4j
public class QrCodeService {
    
    @Value("${qr.code.size:300}")
    private int qrCodeSize;
    
    @Value("${qr.code.format:PNG}")
    private String qrCodeFormat;
    
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    
    public String generateQrCode(String data) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize);
            
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, qrCodeFormat, pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            
            return Base64.getEncoder().encodeToString(pngData);
        } catch (WriterException | IOException e) {
            log.error("Error generating QR code for data: {}", data, e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
    
    public String generateQrCodeImage(String data, String fileName) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize);
            
            // Ensure upload directory exists
            Path uploadPath = Paths.get(uploadDir, "qr-codes");
            Files.createDirectories(uploadPath);
            
            Path filePath = uploadPath.resolve(fileName + ".png");
            MatrixToImageWriter.writeToPath(bitMatrix, qrCodeFormat, filePath);
            
            return "/uploads/qr-codes/" + fileName + ".png";
        } catch (WriterException | IOException e) {
            log.error("Error generating QR code image for data: {}", data, e);
            throw new RuntimeException("Failed to generate QR code image", e);
        }
    }
    
    public String generateQrCodeForTicket(Long bookingId, Long ticketId, String ticketCode) {
        String qrData = String.format("LUNAR_TICKET|%d|%d|%s", bookingId, ticketId, ticketCode);
        return generateQrCode(qrData);
    }
    
    public String generateQrCodeImageForTicket(Long bookingId, Long ticketId, String ticketCode) {
        String qrData = String.format("LUNAR_TICKET|%d|%d|%s", bookingId, ticketId, ticketCode);
        String fileName = String.format("ticket_%d_%d_%s", bookingId, ticketId, ticketCode);
        return generateQrCodeImage(qrData, fileName);
    }
    
    public boolean validateQrCode(String qrData, Long expectedBookingId, Long expectedTicketId) {
        try {
            String[] parts = qrData.split("\\|");
            if (parts.length != 4 || !"LUNAR_TICKET".equals(parts[0])) {
                return false;
            }
            
            Long bookingId = Long.parseLong(parts[1]);
            Long ticketId = Long.parseLong(parts[2]);
            
            return bookingId.equals(expectedBookingId) && ticketId.equals(expectedTicketId);
        } catch (Exception e) {
            log.error("Error validating QR code: {}", qrData, e);
            return false;
        }
    }
}
