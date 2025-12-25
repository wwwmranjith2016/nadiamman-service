package com.billflow.controller;

import com.billflow.service.PdfProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class PdfProcessingController {

    private final PdfProcessingService pdfProcessingService;

    @PostMapping("/extract-text")
    public ResponseEntity<Map<String, Object>> extractTextFromPdf(@RequestParam("file") MultipartFile file) {
        try {
            log.info("Received PDF file for text extraction: {}", file.getOriginalFilename());

            String extractedText = pdfProcessingService.extractTextFromPdf(file);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("text", extractedText);
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", file.getSize());

            log.info("Successfully extracted {} characters from PDF", extractedText.length());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid file provided: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            log.error("Error processing PDF file: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to process PDF file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}