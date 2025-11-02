package com.skillexchange.platform.controller;

import com.skillexchange.platform.entity.Certificate;
import com.skillexchange.platform.service.CertificateService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping("/generate/{exchangeRequestId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<Certificate> generateCertificate(@PathVariable String exchangeRequestId) {
        try {
            Optional<Certificate> certificateOpt = certificateService.generateCertificate(exchangeRequestId);
            
            if (certificateOpt.isPresent()) {
                return ResponseEntity.ok(certificateOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().header("Error", e.getMessage()).build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/download/{certificateId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<Resource> downloadCertificate(@PathVariable String certificateId) {
        try {
            Optional<Resource> resourceOpt = certificateService.getCertificateFile(certificateId);
            
            if (resourceOpt.isPresent() && resourceOpt.get().exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + certificateId + ".pdf\"")
                        .body(resourceOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('STUDENT') or hasRole('UNIVERSITY_ADMIN') or hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<List<Certificate>> getUserCertificates() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // Assuming username is the user ID

        List<Certificate> certificates = certificateService.getCertificatesForUser(userId);
        return ResponseEntity.ok(certificates);
    }
}