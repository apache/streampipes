//package de.fzi.cep.sepa.rest;
//
//import javax.ws.rs.GET;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.MediaType;
//
//import de.fzi.cep.sepa.messages.NotificationType;
//import de.fzi.cep.sepa.model.client.VirtualSensor;
//import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
//import de.fzi.sepa.model.client.util.Utils;
//
//@Path("/block")
//public class VirtualSensorImpl extends AbstractRestInterface implements de.fzi.cep.sepa.rest.api.VirtualSensor{
//
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	@Override
//	public String getVirtualSensors() {
//		return toJson(pipelineStorage.getVirtualSensors());
//	}
//
//	@POST
//	@Produces(MediaType.APPLICATION_JSON)
//	@Override
//	public String addVirtualSensor(String virtualSensorDescription) {
//		pipelineStorage.storeVirtualSensor(Utils.getGson().fromJson(virtualSensorDescription, VirtualSensor.class));
//		return constructSuccessMessage(NotificationType.VIRTUAL_SENSOR_STORAGE_SUCCESS.uiNotification());
//	}
//
//}
