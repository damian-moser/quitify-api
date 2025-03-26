package ch.gibb.quitify.service.ocr;

import ch.gibb.quitify.dto.ReceiptDto;
import ch.gibb.quitify.enums.Currency;
import ch.gibb.quitify.service.util.UtilService;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OCRServiceImpl implements OCRService {

    private static final Logger logger = LogManager.getLogger(OCRServiceImpl.class);

    private final Tesseract tesseract;
    private final UtilService utilService;

    public OCRServiceImpl(Tesseract tesseract, UtilService utilService) {
        this.tesseract = tesseract;
        this.utilService = utilService;
    }

    @Override
    public ReceiptDto parseReceipt(MultipartFile multipartFile) throws IOException, TesseractException {
        File file = null;
        try {
            file = this.utilService.saveAsTempFile(multipartFile);
            String result = tesseract.doOCR(file);

            Date date = this.extractDate(result);
            String name = this.extractRestaurantName(result);
            float sum = this.extractSum(result);
            String currency = this.extractCurrency(result);

            logger.info("[OCR] Image successfully processed");
            return new ReceiptDto(date, name, sum, currency, result);
        } catch (IOException | TesseractException ex) {
            logger.error("[OCR] Error while processing image: {}", ex.getMessage(), ex);
            throw ex;

        } finally {
            if (file != null && file.exists()) {
                try {
                    Files.delete(file.toPath());
                    logger.info("[OCR] Temp image deleted: {}", file.getAbsolutePath());
                } catch (IOException ex) {
                    logger.warn("[OCR] Error while deleting temp image: {}", ex.getMessage(), ex);
                }
            }
        }
    }

    private Date extractDate(String result) {
        String dateRegex = "(\\d{1,2})[./](\\d{1,2})[./](\\d{4})";
        Pattern pattern = Pattern.compile(dateRegex);
        Matcher matcher = pattern.matcher(result);

        if (matcher.find() && matcher.groupCount() >= 3) {
            String day = matcher.group(1).length() == 1 ? "0" + matcher.group(1) : matcher.group(1);
            String month = matcher.group(2).length() == 1 ? "0" + matcher.group(2) : matcher.group(2);
            String year = matcher.group(3);

            String dateString = day + "." + month + "." + year;

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                return dateFormat.parse(dateString);
            } catch (Exception ex) {
                logger.warn("[OCR] Error while parsing date: {}", ex.getMessage(), ex);
            }
        }
        return new Date();
    }

    private String extractRestaurantName(String result) {
        String nameRegex = "(»[A-Za-zÄÖÜäöüß ]+«|[A-Za-zÄÖÜäöüß ]+\\s?[A-Za-zÄÖÜäöüß]+)";
        Pattern pattern = Pattern.compile(nameRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(result);

        if (matcher.find() && matcher.groupCount() >= 1) {
            return matcher.group(1).trim().replaceAll("\n", " ");
        }
        return "Unbekannter Restaurantname";
    }

    private float extractSum(String result) {
        String sumRegex = "(total|totl|summe|sume|sum|gesamt|endbetrag)[^\\d]*(\\d+[,.]?\\d*)";
        Pattern pattern = Pattern.compile(sumRegex, Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(result);
        if (matcher.find() && matcher.groupCount() >= 2) {
            String sumString = matcher.group(2);
            sumString = sumString.replace(",", ".");

            try {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMANY);
                DecimalFormat decimalFormat = new DecimalFormat("#0.00", symbols);
                Number parsedNumber = decimalFormat.parse(sumString);
                return parsedNumber.floatValue();
            } catch (ParseException ex) {
                logger.warn("[OCR] Error while parsing sum: {}", ex.getMessage(), ex);
            }
        }
        return 0;
    }

    private String extractCurrency(String result) {
        String currencyRegex = "(€|USD|EUR|GBP|JPY|CHF|AUD|CAD|CNY|₣|£|¥)";
        Pattern pattern = Pattern.compile(currencyRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(result);

        if (matcher.find() && matcher.groupCount() >= 1) {
            return Currency.fromSymbol(matcher.group(1)).name();
        }
        return Currency.CHF.name();
    }
}
