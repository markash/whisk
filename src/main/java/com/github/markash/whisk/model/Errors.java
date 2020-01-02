package com.github.markash.whisk.model;

import javax.ws.rs.core.Response.Status;

public enum Errors {
    DOES_NOT_EXIST(Status.BAD_REQUEST, 1201, "The file requested does not exist"),
    INVALID_RESOURCE(Status.BAD_REQUEST, 1202, "The file is invalid"),
    RESOURCE_DATA_REQUIRED(Status.BAD_REQUEST, 1203, "The file has no data"),
    RESOURCE_FILE_NAME_REQUIRED(Status.BAD_REQUEST, 1204, "The file name is required"),
    PERSISTENCE_ERROR(Status.BAD_REQUEST, 1205, "The file could not be read or written"),
    TRANSFORMATION_ERROR(Status.BAD_REQUEST, 1206, "The file could not be transformed"),

    UNKNOWN(Status.BAD_REQUEST, 12099, "Unknown error")
    ;

    private final Status status;
    private final int errorCode;
    private final String errorMessage;

    private Errors(
        final Status status, 
        final int errorCode,
        final String errorMessage) {

        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * @return the errorCode
     */
    public int getErrorCode() { return errorCode; }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() { return errorMessage; }

    /**
     * @return the message of the error
     */
    public String getMessage() { return Integer.toString(getErrorCode()) + " : " + getErrorMessage(); }
    
    /**
     * @return the status
     */
    public Status getStatus() { return status; }
}