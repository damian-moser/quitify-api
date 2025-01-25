package ch.gibb.quitify.service.ocr;

import ch.gibb.quitify.dto.ReceiptDto;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface OCRService {

    ReceiptDto parseReceipt(MultipartFile multipartFile) throws IOException, TesseractException;
}
