package de.fzi.cep.sepa.rest.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.fzi.cep.sepa.appstore.shared.BundleInfo;
import de.fzi.cep.sepa.commons.config.ConfigurationManager;
import de.fzi.cep.sepa.manager.operations.Operations;

@Path("/v2/users/{username}/marketplace")
public class AppStore extends AbstractRestInterface {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAvailableApps() {
		return ok(Operations.getAvailableApps());
	}
	
	@POST
	@Path("/install")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response installApp(@PathParam("username") String username, BundleInfo bundleInfo) {
		return ok(Operations.installApp(username, bundleInfo));
	}
	
	@POST
	@Path("/uninstall")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response uninstallApp(@PathParam("username") String username, BundleInfo bundleInfo) {
		return ok(Operations.uninstallApp(username, bundleInfo));
	}
	
	@GET
	@Path("/pods")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTargetPods() {
		return ok(ConfigurationManager.getWebappConfigurationFromProperties().getPodUrls());
	}
	
}
