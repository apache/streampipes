package org.streampipes.container.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.streampipes.container.declarer.InvocableDeclarer;
import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.init.RunningInstances;
import org.streampipes.container.util.Util;
import org.streampipes.model.NamedSEPAElement;
import org.streampipes.model.impl.graph.SecInvocation;

@Path("/sec")
public class SecElement extends InvocableElement<SecInvocation, SemanticEventConsumerDeclarer> {

    public SecElement() {
        super(SecInvocation.class);
    }

    @Override
    protected List<SemanticEventConsumerDeclarer> getElementDeclarers() {
        return DeclarersSingleton.getInstance().getConsumerDeclarers();
    }

    @Override
    protected String getInstanceId(String uri, String elementId) {
        return Util.getInstanceId(uri, "sec", elementId);
    }

    @GET
    @Path("{elementId}/{runningInstanceId}")
    @Produces(MediaType.TEXT_HTML)
    public Response getHtml(@PathParam("elementId") String elementId, @PathParam("runningInstanceId") String runningInstanceId) {

        InvocableDeclarer runningInstance = RunningInstances.INSTANCE.getInvocation(runningInstanceId);
        NamedSEPAElement description = RunningInstances.INSTANCE.getDescription(runningInstanceId);

        if (runningInstance != null && runningInstance instanceof SemanticEventConsumerDeclarer && description != null
                && description instanceof SecInvocation) {

            SemanticEventConsumerDeclarer instanceDeclarer = (SemanticEventConsumerDeclarer) runningInstance;
            SecInvocation desctionDeclarer = (SecInvocation) description;


            return getResponse(instanceDeclarer.getHtml(desctionDeclarer));


        } else {
            return getResponse("Error in element " + elementId);
       }
    }

    private Response getResponse(String text) {
        return Response.ok() //200
                    .entity(text)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .header("Access-Control-Allow-Credentials", "false")
                    .header("Access-Control-Max-Age", "60")
                    .allow("OPTIONS").build();
    }
}
