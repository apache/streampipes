package de.fzi.cep.sepa.client.container.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import de.fzi.cep.sepa.client.container.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.container.init.EmbeddedModelSubmitter;
import de.fzi.cep.sepa.client.html.HTMLGenerator;
import de.fzi.cep.sepa.client.html.page.WelcomePageGeneratorImpl;

@Path("/")
public class WelcomePage {

	@GET
	public String getWelcomePage() {
        WelcomePageGeneratorImpl welcomePage = new WelcomePageGeneratorImpl(EmbeddedModelSubmitter.getBaseUri(), DeclarersSingleton.getInstance().getDeclarers());
        HTMLGenerator html = new HTMLGenerator(welcomePage.buildUris());
		return html.buildHtml();
	}
}
