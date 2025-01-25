package ch.gibb.quitify.controller;

import ch.gibb.quitify.service.ocr.OCRServiceImpl;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/ocr")
public class OCRController {

    private final OCRServiceImpl ocrService;

    public OCRController(OCRServiceImpl ocrService) {
        this.ocrService = ocrService;
    }

    @GetMapping
    public ResponseEntity<?> parseReceipt(@RequestParam MultipartFile multipartFile) {
        try {
            return ResponseEntity.ok(this.ocrService.parseReceipt(multipartFile));
        } catch (TesseractException | IOException ex) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
