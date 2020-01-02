package com.github.markash.whisk;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.markash.whisk.config.Configuration;
import com.github.markash.whisk.exception.ProcessingException;
import com.github.markash.whisk.model.Error;
import com.github.markash.whisk.model.Errors;
import com.github.markash.whisk.service.impl.FirebaseFileService;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logging.Logger;

@Path("/template")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TemplateResource {
    private final Logger logger = Logger.getLogger(getClass());
    
    @Inject
    Configuration configuration;
    
    @Inject
    FirebaseFileService firebaseService;

    @GET
    @Path("/{fileName}")
    @Counted(
        name = "templateRetrievalCounted", 
        description = "How many template retrievals have been performed.")
    @Timed(
        name = "templateRetrievalTimed", 
        description = "A measure how long it takes to perform the template retrieval.", unit = MetricUnits.MILLISECONDS)
    public Response retrieve(
        @PathParam("fileName") String fileName) {

        try {

            return 
                Response
                    .ok(firebaseService.retrieveFile(fileName))
                    .build();

        } catch (ProcessingException e) {
            logger.errorv(e, "Unable to retrieve file {0}", fileName);

            return 
                new Error()
                    .withError(e.getError())
                    .withAdditionalMessage(e.getAdditionalMessage())
                    .buildResponse();
        }
    }

    @POST
    @Timed(
        name = "templateStorageTimed", 
        description = "A measure how long it takes to perform the template storage.", unit = MetricUnits.MILLISECONDS)
    public Response save(
        final BinaryResource resource) {

        if (resource == null) {
            
            return 
                new Error()
                    .withError(Errors.INVALID_RESOURCE)
                    .buildResponse();
        }

        if (!resource.hasBinaryData()) {

            return 
                new Error()
                    .withError(Errors.RESOURCE_DATA_REQUIRED)
                    .buildResponse();
        }
        
        if (resource.getFileName() == null || resource.getFileName().trim().isEmpty()) {

            return 
                new Error()
                    .withError(Errors.RESOURCE_FILE_NAME_REQUIRED)
                    .buildResponse();
        }

        try {

            return 
                Response
                    .ok(firebaseService.saveFile(resource))
                    .build();
        
        } catch (IllegalStateException | IOException e) {
            logger.errorv(e, "Unable to save file {0}", resource.getFileName());

            return 
                new Error()
                    .withError(Errors.PERSISTENCE_ERROR)
                    .buildResponse();
        }
    }

    @GET
    public Response list() {

        final File[] files = 
            this.configuration.getAddress()
                .toFile()
                .listFiles((dir, name) -> (name != null && name.endsWith(".docx")));

        final List<BinaryResource> resources = 
            Arrays.stream(files)
                .map(BinaryResource::withFileName)
                .collect(Collectors.toList());

        return Response.ok(resources).build();
    }
}