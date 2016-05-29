package de.fzi.cep.sepa.client.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import de.fzi.cep.sepa.client.html.HTMLGenerator;
import de.fzi.cep.sepa.client.html.page.WelcomePageGeneratorImpl;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.init.ModelSubmitter;

@Path("/")
public class WelcomePage {

	@GET
	public String getWelcomePage() {
        WelcomePageGeneratorImpl welcomePage = new WelcomePageGeneratorImpl(DeclarersSingleton.getInstance().getBaseUri(), DeclarersSingleton.getInstance().getDeclarers());
        HTMLGenerator html = new HTMLGenerator(welcomePage.buildUris());
		return html.buildHtml();
	}
}
