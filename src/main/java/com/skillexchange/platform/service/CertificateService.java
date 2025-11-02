package com.skillexchange.platform.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.properties.TextAlignment;
import com.skillexchange.platform.entity.Certificate;
import com.skillexchange.platform.entity.User;
import com.skillexchange.platform.entity.ExchangeRequest;
import com.skillexchange.platform.repository.CertificateRepository;
import com.skillexchange.platform.repository.UserRepository;
import com.skillexchange.platform.repository.ExchangeRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.List;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;
    
    @Value("${certificate.storage.path:certificates}")
    private String certificateStoragePath;

    public CertificateService(CertificateRepository certificateRepository,
                            UserRepository userRepository,
                            ExchangeRequestRepository exchangeRequestRepository) {
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
        this.exchangeRequestRepository = exchangeRequestRepository;
    }

    public Optional<Certificate> generateCertificate(String exchangeRequestId) throws IOException {
        Optional<ExchangeRequest> exchangeRequestOpt = exchangeRequestRepository.findById(exchangeRequestId);
        
        if (exchangeRequestOpt.isPresent()) {
            ExchangeRequest exchangeRequest = exchangeRequestOpt.get();
            
            // Check if exchange is completed
            if (exchangeRequest.getStatus() != ExchangeRequest.RequestStatus.COMPLETED) {
                throw new IllegalStateException("Cannot generate certificate for incomplete exchange");
            }
            
            // Check if certificate already exists
            if (certificateRepository.existsByExchangeRequest(exchangeRequest)) {
                return certificateRepository.findByExchangeRequest(exchangeRequest);
            }
            
            // Generate certificate for requester
            generateCertificateForUser(exchangeRequest, exchangeRequest.getRequester());
            
            // Generate certificate for recipient
            generateCertificateForUser(exchangeRequest, exchangeRequest.getRecipient());
            
            // Return the certificate for the requester
            return certificateRepository.findByExchangeRequest(exchangeRequest);
        }
        
        return Optional.empty();
    }
    
    private void generateCertificateForUser(ExchangeRequest exchangeRequest, User user) throws IOException {
        String title = "Skill Exchange Completion Certificate";
        String description = String.format(
            "This certificate is awarded to %s for successfully completing a skill exchange with %s. " +
            "The exchange involved %s (offered) and %s (requested) on %s.",
            user.getUsername(),
            exchangeRequest.getRequester().getId().equals(user.getId()) ? 
                exchangeRequest.getRecipient().getUsername() : exchangeRequest.getRequester().getUsername(),
            exchangeRequest.getOfferedSkill().getName(),
            exchangeRequest.getRequestedSkill().getName(),
            exchangeRequest.getCompletedAt().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
        );
        
        Certificate certificate = new Certificate(user, exchangeRequest, title, description);
        
        // Generate PDF
        byte[] pdfBytes = createCertificatePDF(certificate);
        
        // Save PDF to file system
        String fileName = String.format("certificate_%s_%s.pdf", user.getId(), exchangeRequest.getId());
        Path certificatePath = Paths.get(certificateStoragePath, fileName);
        
        // Ensure directory exists
        certificatePath.getParent().toFile().mkdirs();
        
        // Write PDF to file
        java.nio.file.Files.write(certificatePath, pdfBytes);
        
        // Set certificate URL
        certificate.setCertificateUrl(certificatePath.toString());
        
        // Save certificate entity
        certificateRepository.save(certificate);
    }
    
    private byte[] createCertificatePDF(Certificate certificate) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Add title
        Paragraph title = new Paragraph("CERTIFICATE OF COMPLETION")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(24)
            .setFontColor(ColorConstants.BLUE);
        document.add(title);
        
        // Add spacing
        document.add(new Paragraph("\n"));
        
        // Add recipient name
        Paragraph recipient = new Paragraph("This is presented to")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(16);
        document.add(recipient);
        
        Paragraph recipientName = new Paragraph(certificate.getRecipient().getUsername())
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(20)
            .setFontColor(ColorConstants.RED);
        document.add(recipientName);
        
        // Add spacing
        document.add(new Paragraph("\n"));
        
        // Add description
        Paragraph description = new Paragraph(certificate.getDescription())
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(12);
        document.add(description);
        
        // Add spacing
        document.add(new Paragraph("\n"));
        
        // Add issued date
        Paragraph issuedDate = new Paragraph("Issued on: " + certificate.getIssuedAt().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")))
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(12);
        document.add(issuedDate);
        
        // Add signature line
        document.add(new Paragraph("\n"));
        Paragraph signature = new Paragraph("___________________\nSkillExchange Platform")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(12);
        document.add(signature);
        
        document.close();
        
        return baos.toByteArray();
    }
    
    public Optional<Resource> getCertificateFile(String certificateId) throws IOException {
        Optional<Certificate> certificateOpt = certificateRepository.findById(certificateId);
        
        if (certificateOpt.isPresent()) {
            Certificate certificate = certificateOpt.get();
            Path certificatePath = Paths.get(certificate.getCertificateUrl());
            return Optional.of(new UrlResource(certificatePath.toUri()));
        }
        
        return Optional.empty();
    }
    
    public List<Certificate> getCertificatesForUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(certificateRepository::findByRecipient).orElse(List.of());
    }
}