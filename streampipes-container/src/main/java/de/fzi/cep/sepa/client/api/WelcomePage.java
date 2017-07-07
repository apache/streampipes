package de.fzi.cep.sepa.client.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.fzi.cep.sepa.client.html.HTMLGenerator;
import de.fzi.cep.sepa.client.html.JSONGenerator;
import de.fzi.cep.sepa.client.html.page.WelcomePageGeneratorImpl;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;

@Path("/")
public class WelcomePage {

	@GET
    @Produces(MediaType.TEXT_HTML)
	public String getWelcomePageHtml() {
        WelcomePageGeneratorImpl welcomePage = new WelcomePageGeneratorImpl(DeclarersSingleton.getInstance().getBaseUri(), DeclarersSingleton.getInstance().getDeclarers());
        HTMLGenerator html = new HTMLGenerator(welcomePage.buildUris());
		return html.buildHtml();
	}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
	public String getWelcomePageJson() {
        WelcomePageGeneratorImpl welcomePage = new WelcomePageGeneratorImpl(DeclarersSingleton.getInstance().getBaseUri(), DeclarersSingleton.getInstance().getDeclarers());    
        JSONGenerator json = new JSONGenerator(welcomePage.buildUris());
		return json.buildJson();
	}
}
