package com.billflow.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class PdfProcessingService {

    public String extractTextFromPdf(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new IllegalArgumentException("File must be a PDF");
        }

        try (InputStream inputStream = file.getInputStream()) {
            byte[] bytes = inputStream.readAllBytes();
            PDDocument document = Loader.loadPDF(bytes);

            log.info("Processing PDF with {} pages", document.getNumberOfPages());

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            String text = stripper.getText(document);
            log.info("Extracted {} characters of text from PDF", text.length());

            if (text.trim().isEmpty()) {
                throw new IOException("No readable text found in PDF. This appears to be a scanned document.");
            }

            return text;
        } catch (IOException e) {
            log.error("Error processing PDF file: {}", e.getMessage());
            throw new IOException("Failed to process PDF file: " + e.getMessage(), e);
        }
    }
}