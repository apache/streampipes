package org.streampipes.container.api;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.streampipes.container.init.RunningInstances;
import org.streampipes.container.util.Util;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;

import org.streampipes.container.declarer.Declarer;
import org.streampipes.container.declarer.InvocableDeclarer;
import org.streampipes.container.transform.Transformer;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.Response;

public abstract class InvocableElement<I extends InvocableStreamPipesEntity, D extends Declarer> extends Element<D> {

    protected abstract List<D> getElementDeclarers();
    protected abstract String getInstanceId(String uri, String elementId);

    protected Class<I> clazz;

    public InvocableElement(Class<I> clazz) {
        this.clazz = clazz;
    }

    //TODO remove the Form paramerter thing
    @POST
    @Path("{elementId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
//    public String invokeRuntime(@PathParam("elementId") String elementId, String payload) {
    public String invokeRuntime(@PathParam("elementId") String elementId, @FormParam("json") String payload) {

        try {
            I graph = Transformer.fromJsonLd(clazz, payload);
            InvocableDeclarer declarer = (InvocableDeclarer) getDeclarerById(elementId);

            if (declarer != null) {
//                String runningInstanceId = Util.getInstanceId(graph.getElementId(), "sepa", elementId);
                String runningInstanceId = getInstanceId(graph.getElementId(), elementId);
                RunningInstances.INSTANCE.add(runningInstanceId, graph, declarer.getClass().newInstance());
                Response resp = RunningInstances.INSTANCE.getInvocation(runningInstanceId).invokeRuntime(graph);
                return Util.toResponseString(resp);
            }
        } catch (RDFParseException | IOException | RepositoryException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return Util.toResponseString(new Response(elementId, false, e.getMessage()));
        }

        return Util.toResponseString(elementId, false, "Could not find the element with id: " + elementId);
    }

    @DELETE
    @Path("{elementId}/{runningInstanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String detach(@PathParam("elementId") String elementId, @PathParam("runningInstanceId") String runningInstanceId) {

        InvocableDeclarer runningInstance = RunningInstances.INSTANCE.getInvocation(runningInstanceId);

        if (runningInstance != null) {
            Response resp = runningInstance.detachRuntime(runningInstanceId);

            if (resp.isSuccess()) {
                RunningInstances.INSTANCE.remove(runningInstanceId);
            }

            return Util.toResponseString(resp);
        }

        return Util.toResponseString(elementId, false, "Could not find the running instance with id: " + runningInstanceId);
    }

}

