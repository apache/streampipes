package org.streampipes.connect.rest;


import org.rendersnake.HtmlCanvas;
import org.streampipes.connect.init.Config;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.name;

@Path("/")
public class WelcomePage {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getWelcomePageHtml() {
		return buildHtml();
	}

	private static String buildHtml() {
		HtmlCanvas html = new HtmlCanvas();
		try {
			html
					.head()
					.title()
					.content("StreamPipes Connector Container")
					._head()
					.body()
						.h1().write("Connector Container with ID " + Config.CONNECTOR_CONTAINER_ID + " is running")._h1()
					._body();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return html.toHtml();
	}
}
