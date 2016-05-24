package de.fzi.cep.sepa.client.container.rest;

import de.fzi.cep.sepa.client.container.init.DeclarersSingleton;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/sepa")
public class SepaElement extends InvocableElement<SepaInvocation, SemanticEventProcessingAgentDeclarer> {

    public SepaElement() {

        super(SepaInvocation.class);
    }

    @Override
    protected List<SemanticEventProcessingAgentDeclarer> getElementDeclarers() {
        return DeclarersSingleton.getInstance().getEpaDeclarers();
    }

//    @GET
//    @Path("{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String getDescription(@PathParam("id") String elementId) {
//        List<SemanticEventProcessingAgentDeclarer> sepas = DeclarersSingleton.getInstance().getEpaDeclarers();
//        return getJsonLd(sepas, elementId);
//    }

//    //TODO remove the Form paramerter thing
//    @POST
//    @Path("{elementId}")
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//    @Produces(MediaType.APPLICATION_JSON)
////    public String invokeRuntime(@PathParam("elementId") String elementId, String payload) {
//    public String invokeRuntime(@PathParam("elementId") String elementId, @FormParam("json") String payload) {
//
//        try {
//            SepaInvocation graph = Transformer.fromJsonLd(SepaInvocation.class, payload);
//            List<SemanticEventProcessingAgentDeclarer> sepas = DeclarersSingleton.getInstance().getEpaDeclarers();
//            SemanticEventProcessingAgentDeclarer sepa = (SemanticEventProcessingAgentDeclarer) getDeclarerById(sepas, elementId);
//
//            SepaDescription graph1 = sepa.declareModel();
//            if (sepa != null) {
//                String runningInstanceId = Util.getInstanceId(graph.getElementId(), "sepa", elementId);
//                RunningInstances.INSTANCE.add(runningInstanceId, graph, sepa.getClass().newInstance());
//                Response resp = RunningInstances.INSTANCE.getInvocation(runningInstanceId).invokeRuntime(graph);
//                return Util.toResponseString(resp);
//            }
//        } catch (RDFParseException | IOException | RepositoryException | InstantiationException | IllegalAccessException e) {
//            e.printStackTrace();
//            return Util.toResponseString(new Response(elementId, false, e.getMessage()));
//        }
//
//        return Util.toResponseString(elementId, false, "Could not find the element with id: " + elementId);
//    }

//    @DELETE
//    @Path("{elementId}/{runningInstanceId}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String detach(@PathParam("elementId") String elementId, @PathParam("runningInstanceId") String runningInstanceId) {
//
//        InvocableDeclarer runningInstance = RunningInstances.INSTANCE.getInvocation(runningInstanceId);
//
//        if (runningInstance != null) {
//            Response resp = runningInstance.detachRuntime(runningInstanceId);
//
//            if (resp.isSuccess()) {
//                RunningInstances.INSTANCE.remove(runningInstanceId);
//            }
//
//            return Util.toResponseString(resp);
//        }
//
//        return Util.toResponseString(elementId, false, "Could not find the running instance with id: " + runningInstanceId);
//    }

}
