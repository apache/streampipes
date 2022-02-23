package org.apache.streampipes.service.base.rest;

import org.apache.streampipes.service.base.StreamPipesServiceBase;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("")
public class ServiceHealthResource {

  @GET()
  @Path("svchealth/{serviceId}")
  public Response healthy(@PathParam("serviceId") String serviceId) {
    if (serviceId.equals(StreamPipesServiceBase.AUTO_GENERATED_SERVICE_ID)) {
      return Response.ok().build();
    } else {
      return Response.status(404).build();
    }
  }
}
