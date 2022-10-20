package org.apache.streampipes.container.api;

import org.apache.streampipes.container.monitoring.SpMonitoringManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("monitoring")
public class MonitoringResource extends AbstractExtensionsResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getMonitoringInfos() {
    try {
      return ok(SpMonitoringManager.INSTANCE.getMonitoringInfo());
    } finally {
      //SpLogManager.INSTANCE.clearAllLogs();
    }
  }
}
