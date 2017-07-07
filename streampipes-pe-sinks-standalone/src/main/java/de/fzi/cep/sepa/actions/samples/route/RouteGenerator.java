package de.fzi.cep.sepa.actions.samples.route;

import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.onLoad;

import java.io.IOException;
import java.util.Arrays;

import org.rendersnake.HtmlCanvas;

import de.fzi.cep.sepa.actions.samples.HtmlGenerator;

public class RouteGenerator extends HtmlGenerator<RouteParameters>{

	public RouteGenerator(RouteParameters actionParameters) {
		super(actionParameters);
	}

	@Override
	protected HtmlCanvas buildHtmlCanvas() {
		HtmlCanvas canvas = new HtmlCanvas();
		try {
			canvas = getStandardizedHeader(canvas, Arrays.asList("stomp.js", "route/route.js"), Arrays.asList());
			canvas.body(onLoad("buildGoogleMap('" +actionParameters.getUrl() +"', '" +actionParameters.getTopic() +"', '" +actionParameters.getLatitudeName() +"', '" +actionParameters.getLongitudeName() +"', '" +actionParameters.getLabelName() +"')"))   
			   .div(id("container").style("min-width: 310px; height: 700px; margin: 0 auto"))._div();
			canvas = getStandardizedFooter(canvas);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return canvas;
	}

}
