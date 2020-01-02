package com.github.markash.whisk.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.markash.whisk.BinaryResource;
import com.github.markash.whisk.config.Configuration;
import com.github.markash.whisk.config.FirebaseConfiguration;
import com.github.markash.whisk.exception.ProcessingException;
import com.github.markash.whisk.model.Errors;
import com.github.markash.whisk.service.FileService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.StorageException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;

/**
 * A Firebase File Service to retrieve, list and upload files to Firebase Storage
 * @author Mark Ashworth 
 */
@ApplicationScoped
public class FirebaseFileService implements FileService {
    private final Logger logger = Logger.getLogger(getClass());

    private FirebaseConfiguration configuration;


    @Inject
    public FirebaseFileService(
        final Configuration configuration) {

        this.configuration = configuration.getFirebase();
    }

    void initialize(
        @Observes final StartupEvent event) throws IOException {

        logger.infov("Received event {0}", event);
    }
    
    /**
     * Retrieve the file by file name from the Firebase Storage
     * @param fileName The name of the file 
     * @return The binary resource containing the file data
     * @throws ProcessingException If the file could not be loaded or found
     */
    @Override
    @Timed(
        name = "fileRetrievalTimed", 
        description = "A measure how long it takes to perform the file retrieval.", unit = MetricUnits.MILLISECONDS)
    public BinaryResource retrieveFile(
        final String fileName) throws ProcessingException {

        if (fileName == null) {
            throw new ProcessingException(
                Errors.INVALID_RESOURCE, 
                "The fileName is null");
        }

        logger.infov("Before init in retrieveFile");

        try {
            Bucket bucket = 
                StorageClient.getInstance(firebaseApp())
                    .bucket(configuration.getStorageBucket());
            
            for (Blob blob : bucket.list().getValues()) {
                
                if (blob.getName().equals(fileName)) {

                    return retrieveFile(fileName, blob);
                }
            }
        } catch (IOException | StorageException e) {

            throw new ProcessingException(
                Errors.PERSISTENCE_ERROR,
                e.getMessage(), 
                e);
        }

        throw new ProcessingException(
            Errors.DOES_NOT_EXIST,
            "The file name " + fileName + " does not exist");
    }

    /**
     * Save the file to the file store
     * @param resource The binary resource containing the data and file name to store
     * @return The saved binary resource with additional store data
     * @throws IOException If the file could not be stored
     */
    @Override
    @Timed(
        name = "fileStoreTimed", 
        description = "A measure how long it takes to perform the file storage.", unit = MetricUnits.MILLISECONDS)
    public BinaryResource saveFile(
        final BinaryResource resource) throws IOException {

        if (resource == null) {
            throw new IllegalStateException("The binary resource is null and is required");
        }

        if (resource.getFileName() == null || resource.getFileName().trim().isEmpty()) {
            throw new IllegalStateException("The binary resource file name is null and is required");
        }

        byte[] data = resource.getBinaryData();
        if (data == null || data.length == 0) {
            throw new IllegalStateException("The binary resource data is null and is required");
        }

        Bucket bucket = 
            StorageClient.getInstance(firebaseApp())
                .bucket(configuration.getStorageBucket());
        

        
        Blob blob = 
            bucket.create(
                resource.getFileName(), 
                new ByteArrayInputStream(data),
                "application/octet-stream");
        
        return resource
            .withLink(blob.getMediaLink())
            .withCreateTime(blob.getCreateTime())
            .withUpdateTime(blob.getUpdateTime())
            .withSize(blob.getSize())
            .withId(blob.getBlobId().toString());
    }

    private BinaryResource retrieveFile(
        final String fileName,
        final Blob blob) throws IOException {

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            blob.downloadTo(os);
            return 
                new BinaryResource()
                    .withFileName(fileName)
                    .withData(os.toByteArray());
        } 
    }

    FirebaseOptions firebaseOptions() throws IOException {

        return 
            new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .setDatabaseUrl(this.configuration.getDatabaseUrl())
                .setProjectId(this.configuration.getProjectId())
                .setStorageBucket(this.configuration.getStorageBucket())
                .build();
    }

    FirebaseApp firebaseApp() throws IOException {

        boolean applicationExists = 
            FirebaseApp.getApps()
                .stream()
                .map(FirebaseApp::getName)
                .anyMatch(name -> name != null && name.equals(this.configuration.getApplicationName()));

        FirebaseApp app;
        if (applicationExists) {
            app = FirebaseApp.getInstance(this.configuration.getApplicationName());
        } else {
            app = FirebaseApp.initializeApp(firebaseOptions() , this.configuration.getApplicationName());
        }

        return app;
    }
}