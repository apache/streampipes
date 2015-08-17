//package de.fzi.cep.sepa.rest.v2;
//
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.MediaType;
//
//import de.fzi.cep.sepa.rest.api.Visualization;
//import de.fzi.cep.sepa.storage.controller.StorageManager;
//import de.fzi.sepa.model.client.util.Utils;
//
//@Path("/visualizations")
//public class VisualizationImpl implements Visualization {
//
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	public String getRunningVisualizations() {
//		return Utils.getGson().toJson(StorageManager.INSTANCE.getPipelineStorageAPI().getRunningVisualizations());
//	}
//
//}
