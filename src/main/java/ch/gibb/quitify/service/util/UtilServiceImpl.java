package ch.gibb.quitify.service.util;

import ch.gibb.quitify.service.util.UtilService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class UtilServiceImpl implements UtilService {

    @Override
    public File saveAsTempFile(MultipartFile file) throws IOException {
        final String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        File tempFile = File.createTempFile("ocr-", "-" + originalFilename);
        file.transferTo(tempFile);
        return tempFile;
    }
}
