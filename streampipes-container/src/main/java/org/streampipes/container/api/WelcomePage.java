package org.streampipes.container.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.streampipes.container.html.HTMLGenerator;
import org.streampipes.container.html.JSONGenerator;
import org.streampipes.container.html.page.WelcomePageGeneratorImpl;
import org.streampipes.container.init.DeclarersSingleton;

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
