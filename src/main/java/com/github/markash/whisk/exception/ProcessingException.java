package com.github.markash.whisk.exception;

import com.github.markash.whisk.model.Errors;

public class ProcessingException extends Exception {
    private static final long serialVersionUID = 1L;

    private final Errors error;
    private final String additionalMessage;

    public ProcessingException(
        final Errors error,
        final String additionalMessage, 
        final Throwable cause) {

        super(
            additionalMessage == null ? error.getMessage() : error.getMessage() + " : " + additionalMessage, 
            cause);

        this.error = error;
        this.additionalMessage = additionalMessage;
    }

    public ProcessingException(
        final Errors error,
        final String additionalMessage) {

        super(
            additionalMessage == null ? error.getMessage() : error.getMessage() + " : " + additionalMessage);

        this.error = error;
        this.additionalMessage = additionalMessage;
    }

    /**
     * The error that caused the processing exception
     * @return the error
     */
    public Errors getError() { return error; }

    /**
     * The additional message describing the exception
     * @return the additionalMessage
     */
    public String getAdditionalMessage() { return additionalMessage; }
}