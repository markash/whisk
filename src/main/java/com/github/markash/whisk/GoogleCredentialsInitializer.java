package com.github.markash.whisk;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class GoogleCredentialsInitializer {
    private final Logger logger = Logger.getLogger(getClass());

    /**
     * Transfer the Google Credentials from a environment variable to a file
     * for use by the API
     * @param event The startup event
     */
    void onStart(
        final @Observes StartupEvent event) {               
        
        logger.info("Reading the GOOGLE_APPLICATION_CREDENTIALS environment variable");
        
        String variable = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        if (variable == null || variable.trim().isEmpty()) {
            logger.info("Skipping Google Credentials setup because the GOOGLE_APPLICATION_CREDENTIALS is not set");
            return;
        }

        Path credentialsPath = Paths.get(variable);

        logger.infov("Using GOOGLE_APPLICATION_CREDENTIALS environment variable: {0}", credentialsPath.toFile().getAbsolutePath());

        logger.info("Reading the GOOGLE_APPLICATION_CREDENTIALS_JSON environment variable");

        variable = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON");
        if (variable == null || variable.trim().isEmpty()) {
            logger.info("Skipping Google Credentials setup because the GOOGLE_APPLICATION_CREDENTIALS_JSON is not set");
            return;
        }

        logger.info("Transferring the GOOGLE_APPLICATION_CREDENTIALS_JSON environment variable to file.");

        try (OutputStream fos = new FileOutputStream(credentialsPath.toFile())) {
        
            fos.write(variable.getBytes(StandardCharsets.UTF_8));
            fos.flush();

            logger.info("Transfer of  GOOGLE_APPLICATION_CREDENTIALS_JSON environment variable complete.");
        } catch (IOException e) {

            logger.errorv(e, "Unable to transfer Google Credentials to file {0} for use by API", credentialsPath.toFile().getAbsolutePath());
        }
    }
}
