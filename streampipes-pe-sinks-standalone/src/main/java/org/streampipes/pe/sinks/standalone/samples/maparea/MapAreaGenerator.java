package org.streampipes.pe.sinks.standalone.samples.maparea;

import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.onClick;
import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.StringResource;

import org.streampipes.pe.sinks.standalone.samples.HtmlGenerator;

public class MapAreaGenerator extends HtmlGenerator<MapAreaParameters> {

	public MapAreaGenerator(MapAreaParameters actionParameters) {
		super(actionParameters);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected HtmlCanvas buildHtmlCanvas() {
		HtmlCanvas canvas = new HtmlCanvas();
		try {
			canvas.div()
			   .script(type("text/javascript"))
					.render(new StringResource("stomp.js",false))
			   		.render(new StringResource("maparea/gmaps.js",false))
			   ._script()
			   .button(onClick("buildGoogleMap('" +actionParameters.getUrl() +"', '" +actionParameters.getTopic() +"', '" +actionParameters.getLatitudeNw() +"', '" +actionParameters.getLongitudeNw() +"', '" +actionParameters.getLatitudeSe() +"', '" +actionParameters.getLongitudeSe() +"', '" +actionParameters.getLabelName() +"')").style("btn btn-danger")).write("Load")._button()   
			   .div(id("container").style("min-width: 310px; height: 700px; margin: 0 auto"))._div()
			._div();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return canvas;
	}

}
