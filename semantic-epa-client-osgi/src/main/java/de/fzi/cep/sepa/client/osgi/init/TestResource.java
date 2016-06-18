package de.fzi.cep.sepa.client.osgi.init;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("status")
public class TestResource {
	    @GET
	    @Produces("text/plain")
	    public String getStatus() {
	        return "active";
	    }
	
}
