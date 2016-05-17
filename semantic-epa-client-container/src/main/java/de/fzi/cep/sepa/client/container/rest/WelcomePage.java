package de.fzi.cep.sepa.client.container.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class WelcomePage {

	@GET
	public String getWelcomePage() {
		return "";
	}
}
