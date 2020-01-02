package com.github.markash.whisk.util;

import javax.ws.rs.core.Response;

public class ResponseHelper {

    private ResponseHelper() {}

    public static boolean isOK(
        final Response response) {

        return response != null && response.getStatus() == Response.Status.OK.getStatusCode();
    }
}