package de.fzi.cep.sepa.rest.impl;

import de.fzi.cep.sepa.kpi.KpiRequest;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.model.client.pipeline.PipelineOperationStatus;
import de.fzi.cep.sepa.rest.api.IKpiPipeline;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by riemer on 03.10.2016.
 */
@Path("/v2/kpis")
public class KpiPipeline extends AbstractRestInterface implements IKpiPipeline {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response addKpi(KpiRequest kpiRequest) {
        try {
            PipelineOperationStatus status = Operations.createAndStartKpiFromPipeline(kpiRequest);
            return ok(status);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{kpiId}")
    @Override
    public Response deleteKpi(@PathParam("kpiId") String kpiId) {
        try {
            PipelineOperationStatus status = Operations.stopAndDeletePipeline(kpiId);
            return ok(status);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{kpiId}")
    @Override
    public Response updateKpi(KpiRequest kpiRequest) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
