package org.apache.streampipes.rest.impl;

import org.apache.streampipes.manager.monitoring.pipeline.ExtensionsLogProvider;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/adapter-monitoring")
public class AdapterMonitoringResource extends AbstractMonitoringResource {

  @Path("adapter/{elementId}/logs")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getLogInfoForAdapter(@PathParam("elementId") String elementId) {
    return ok(ExtensionsLogProvider.INSTANCE.getLogInfosForAdapter(elementId));
  }

  @Path("adapter/{elementId}/metrics")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getMetricsInfoForAdapter(@PathParam("elementId") String elementId) {
    return ok(ExtensionsLogProvider.INSTANCE.getMetricInfosForAdapter(elementId));
  }
}
