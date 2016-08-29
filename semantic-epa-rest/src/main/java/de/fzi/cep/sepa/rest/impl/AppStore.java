package de.fzi.cep.sepa.rest.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.fzi.cep.sepa.appstore.shared.BundleInfo;
import de.fzi.cep.sepa.commons.config.ConfigurationManager;
import de.fzi.cep.sepa.manager.operations.Operations;

@Path("/v2/users/{username}/marketplace")
public class AppStore extends AbstractRestInterface {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAvailableApps() {
		return toJson(Operations.getAvailableApps());
	}
	
	@POST
	@Path("/install")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String installApp(@PathParam("username") String username, String json) {
		return toJson(Operations.installApp(username, fromJson(json, BundleInfo.class)));
	}
	
	@POST
	@Path("/uninstall")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String uninstallApp(@PathParam("username") String username, String json) {
		return toJson(Operations.uninstallApp(username, fromJson(json, BundleInfo.class)));
	}
	
	@GET
	@Path("/pods")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTargetPods() {
		return toJson(ConfigurationManager.getWebappConfigurationFromProperties().getPodUrls());
	}
	
}
