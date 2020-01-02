package com.github.markash.whisk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.github.markash.whisk.model.Error;
import com.github.markash.whisk.model.Errors;
import com.github.markash.whisk.model.Transformation;
import com.github.markash.whisk.util.ResponseHelper;

import org.jboss.logging.Logger;
import org.wickedsource.docxstamper.DocxStamper;
import org.wickedsource.docxstamper.DocxStamperConfiguration;
import org.wickedsource.docxstamper.api.DocxStamperException;


@Path("transform")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransformResource {
    private final Logger logger = Logger.getLogger(getClass());

    @Inject
    TemplateResource templateResource;

    @POST
    public Response transform(
        final Transformation transformation) {
        
        Response response = 
            templateResource.retrieve(transformation.getTemplateName());
        
        if (!ResponseHelper.isOK(response)) {
            Error error = response.readEntity(Error.class);

            return Response.status(Status.BAD_REQUEST)
                .entity(error)
                .build();
        }

        BinaryResource template = 
            response.readEntity(BinaryResource.class);

        BinaryResource result = 
            new BinaryResource()
                .withFileName(transformation.getResultName());

        response = transformTemplate(template, result, transformation);
        
        if (!ResponseHelper.isOK(response)) {

            return response;
        }

        return templateResource.save(result);
    }

    private Response transformTemplate(
        final BinaryResource template,
        final BinaryResource result,
        final Transformation transformation) {

        try (
            InputStream is = new ByteArrayInputStream(template.getBinaryData()); 
            ByteArrayOutputStream os = new ByteArrayOutputStream()) {
        
            @SuppressWarnings("unchecked")
            DocxStamper<Transformation> stamper = 
                new DocxStamperConfiguration()
                    .build();

            stamper.stamp(is, transformation, os);

            result.withData(os.toByteArray());

        } catch (DocxStamperException e) {

            logger.errorv(e, "Unable to tranform template : {0}", e.getMessage());

            final String additonalMessage =  e.getCause() instanceof IllegalStateException ? e.getCause().getMessage() : null;
            
            return new Error()
                .withError(Errors.TRANSFORMATION_ERROR)
                .withAdditionalMessage(additonalMessage)
                .buildResponse();

        } catch (IOException e) {
            
            logger.error("Unable to transform template : {0}", e.getMessage(), e);
            
            return new Error()
                .withError(Errors.UNKNOWN)
                .buildResponse();

        } catch (Throwable e) {

            logger.error("Unable to tranform template : {0}", e.getMessage(), e);

            return new Error()
                .withError(Errors.TRANSFORMATION_ERROR)
                .buildResponse();
        }

        return Response.ok().build();
    }
}