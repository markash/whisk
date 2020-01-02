package com.github.markash.whisk.model;

import javax.ws.rs.core.Response;

public class Error {
    private int code;
    private String message;
    private String additionalMessage;

    /**
     * The response status code, i.e. 404
     * @return the response status code 
     */
    public int getCode() { return code; }

    public String getMessage() { return message; }

    public String getAdditionalMessage() { return additionalMessage; }

    /**
     * The response status code, i.e. 404
     * @param code the response status code 
     */
    public void setCode(int code) { this.code = code; }

    /**
     * The message containing the error code number and the error message separated with a colon
     * i.e. 456 : This is an error
     * @param message the message to set
     */
    public void setMessage(String message) { this.message = message; }

    /**
     * Any additional message that will assist the client but will not compromise security, i.e. no full stack traces
     * @param additionalMessage the additionalMessage to set
     */
    public void setAdditionalMessage(String additionalMessage) { this.additionalMessage = additionalMessage; }

    public Error withError(final Errors error) {

        setCode(error.getStatus().getStatusCode());
        setMessage(error.getMessage());
        return this;
    }

    public Error withAdditionalMessage(final String additionalMessage) {

        setAdditionalMessage(additionalMessage);
        return this;
    }

    public Response buildResponse() {

        return
            Response.status(getCode())
                .entity(this)
                .build();
    }

    public Response buildResponse(
        final Response response) {

        return
            Response.fromResponse(response)
                .status(getCode())
                .entity(this)
                .build();
    }
}