package com.github.markash.whisk.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.github.markash.whisk.BinaryResource;
import com.github.markash.whisk.config.Configuration;
import com.github.markash.whisk.exception.ProcessingException;
import com.github.markash.whisk.model.Errors;
import com.github.markash.whisk.service.FileService;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logging.Logger;

/**
 * A file service to retrieve, list, upload files to the local file server
 * @author Mark Ashworth
 */
@ApplicationScoped
public class LocalFileService implements FileService {
    private final Logger logger = Logger.getLogger(getClass());
    
    @Inject
    Configuration configuration;

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
        
        final File[] files = 
            this.configuration.getAddress()
                .toFile()
                .listFiles((dir, name) -> (name != null && name.equalsIgnoreCase(fileName)));

        Optional<BinaryResource> resource =
            Arrays.stream(files)
                .map(BinaryResource::withFileName)
                .findFirst();

        if (!resource.isPresent()) {
            throw new ProcessingException(
                Errors.DOES_NOT_EXIST,   
                "The file name " + fileName + " does not exist");
        }

        return retrieveData(resource.get());
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

        Path file = 
            configuration.getAddress()
                .resolve(resource.getFileName());
        
        if (file.toFile().exists()) {
			logger.warnv( 
					"Requested file {0} exists at address {1} and will be overwritten.", 
                    resource.getFileName(),
                    this.configuration.getAddress().toFile().getAbsoluteFile());
        }
        
        try (OutputStream fos = new FileOutputStream(file.toFile())) {
        
            fos.write(resource.getBinaryData());
            fos.flush();

            BasicFileAttributes attributes =
                Files.readAttributes(file, BasicFileAttributes.class);

            return 
                resource
                    .withCreateTime(attributes.creationTime().toMillis()) 
                    .withUpdateTime(attributes.lastModifiedTime().toMillis())
                    .withSize(attributes.size())
                    .withId(attributes.fileKey().toString())
                    .withLink(file.toFile().getAbsolutePath());

        }
    }

    private BinaryResource retrieveData(
        final BinaryResource resource) throws ProcessingException {

        try {

            java.nio.file.Path path = 
                this.configuration.getAddress()
                    .resolve(resource.getFileName());

            resource.withData(Files.readAllBytes(path));

            return resource;

        } catch (IOException | OutOfMemoryError | SecurityException e) {

            throw new ProcessingException(
                Errors.PERSISTENCE_ERROR,
                "Unable to read file data for file " + resource.getFileName(), e);
        }
    }
}