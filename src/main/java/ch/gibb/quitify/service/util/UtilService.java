package ch.gibb.quitify.service.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface UtilService {

    File saveAsTempFile(MultipartFile file) throws IOException;
}
