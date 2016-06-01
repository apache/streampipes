package de.fzi.cep.sepa.client.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.fzi.cep.sepa.client.declarer.Declarer;
import de.fzi.cep.sepa.client.html.HTMLGenerator;
import de.fzi.cep.sepa.client.html.JSONGenerator;
import de.fzi.cep.sepa.client.html.page.WelcomePageGeneratorImpl;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.init.ModelSubmitter;
import jdk.nashorn.internal.runtime.JSONFunctions;

import java.util.ArrayList;
import java.util.List;

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
        //TODO check solution is jsut a quick fix
        WelcomePageGeneratorImpl welcomePage = new WelcomePageGeneratorImpl(DeclarersSingleton.getInstance().getBaseUri(), DeclarersSingleton.getInstance().getDeclarers());
        welcomePage.getDescriptions();

        JSONGenerator json = new JSONGenerator(welcomePage.getDescriptions());
		return json.buildJson();
	}
}
