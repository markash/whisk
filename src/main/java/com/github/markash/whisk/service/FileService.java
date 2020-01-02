package com.github.markash.whisk.service;

import java.io.IOException;

import com.github.markash.whisk.BinaryResource;
import com.github.markash.whisk.exception.ProcessingException;

/**
 * File service to retrieve, list and upload files
 * @author Mark Ashworth
 */
public interface FileService {

    /**
     * Retrieve the file by file name from the file store
     * @param fileName The file name of the file
     * @return The binary resource containing the file data
     * @throws ProcessingException If the file could not be loaded or found
     */
    BinaryResource retrieveFile(
        final String fileName) throws ProcessingException;

    /**
     * Save the file to the file store
     * @param resource The binary resource containing the data and file name to store
     * @return The saved binary resource with additional store data
     * @throws IOException If the file could not be stored
     */
    BinaryResource saveFile(
        final BinaryResource resource) throws IOException;
}