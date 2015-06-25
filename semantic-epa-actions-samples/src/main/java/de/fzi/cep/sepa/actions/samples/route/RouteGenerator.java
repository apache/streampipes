package de.fzi.cep.sepa.actions.samples.route;

import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.onClick;
import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.StringResource;

import de.fzi.cep.sepa.actions.samples.HtmlGenerator;

public class RouteGenerator extends HtmlGenerator<RouteParameters>{

	public RouteGenerator(RouteParameters actionParameters) {
		super(actionParameters);
	}

	@Override
	protected HtmlCanvas buildHtmlCanvas() {
		HtmlCanvas canvas = new HtmlCanvas();
		try {
			canvas.div()
			   .script(type("text/javascript"))
					.render(new StringResource("stomp.js",false))
			   		.render(new StringResource("route/route.js",false))
			   ._script()
			   .button(onClick("buildGoogleMap('" +actionParameters.getUrl() +"', '" +actionParameters.getTopic() +"', '" +actionParameters.getLatitudeName() +"', '" +actionParameters.getLongitudeName() +"', '" +actionParameters.getLabelName() +"')").style("btn btn-danger")).write("Load")._button()   
			   .div(id("container").style("min-width: 310px; height: 700px; margin: 0 auto"))._div()
			._div();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return canvas;
	}

}
