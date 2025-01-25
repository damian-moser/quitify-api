package ch.gibb.quitify.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TesseractConfig {

    @Bean
    public Tesseract tesseract() {
        Tesseract tesseract = new Tesseract();
        tesseract.setLanguage("deu");

        String datapath;
        try {
            var resource = getClass().getClassLoader().getResource("tessdata");
            if (resource == null) {
                throw new IllegalStateException("The 'tessdata' directory could not be found in the classpath.");
            }
            datapath = resource.getPath();

            if (datapath.startsWith("/") && System.getProperty("os.name").toLowerCase().contains("win")) {
                datapath = datapath.substring(1);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to initialize Tesseract due to missing 'tessdata' directory.", ex);
        }

        tesseract.setDatapath(datapath);
        return tesseract;
    }
}
