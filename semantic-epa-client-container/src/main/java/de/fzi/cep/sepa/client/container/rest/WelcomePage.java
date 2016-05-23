package de.fzi.cep.sepa.client.container.rest;

import de.fzi.cep.sepa.client.container.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.container.init.EmbeddedModelSubmitter;
import de.fzi.cep.sepa.html.HTMLGenerator;
import de.fzi.cep.sepa.html.page.WelcomePageGeneratorImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class WelcomePage {

	@GET
	public String getWelcomePage() {
        WelcomePageGeneratorImpl welcomePage = new WelcomePageGeneratorImpl(EmbeddedModelSubmitter.getBaseUri(), DeclarersSingleton.getInstance().getDeclarers());
        HTMLGenerator html = new HTMLGenerator(welcomePage.buildUris());
		return html.buildHtml();
	}
}
