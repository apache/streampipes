package de.fzi.cep.sepa.rest.impl;

import de.fzi.cep.sepa.model.client.messages.NotificationType;
import de.fzi.cep.sepa.model.client.util.Utils;
import de.fzi.cep.sepa.rest.api.IVirtualSensor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/block")
public class VirtualSensor extends AbstractRestInterface implements IVirtualSensor {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getVirtualSensors(@PathParam("username") String username) {
		return ok(getPipelineStorage().getVirtualSensors(username));
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response addVirtualSensor(@PathParam("username") String username, String virtualSensorDescription) {
		de.fzi.cep.sepa.model.client.VirtualSensor vs = Utils.getGson().fromJson(virtualSensorDescription, de.fzi.cep.sepa.model.client.VirtualSensor.class);
		//vs.setPipelineId(UUID.randomUUID().toString());
		vs.setCreatedBy(username);
		getPipelineStorage().storeVirtualSensor(username, vs);
		return constructSuccessMessage(NotificationType.VIRTUAL_SENSOR_STORAGE_SUCCESS.uiNotification());
	}

}
