package de.fzi.cep.sepa.actions.samples.maps;

import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.onClick;
import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.StringResource;

import de.fzi.cep.sepa.actions.samples.HtmlGenerator;

import static org.rendersnake.HtmlAttributesFactory.*;

public class MapsGenerator extends HtmlGenerator<MapsParameters>{

	public MapsGenerator(MapsParameters actionParameters) {
		super(actionParameters);
	}

	@Override
	protected HtmlCanvas buildHtmlCanvas() {
		HtmlCanvas canvas = new HtmlCanvas();
		try {
			canvas.div()
			   .script(type("text/javascript"))
					.render(new StringResource("stomp.js",false))
			   		.render(new StringResource("gMaps.js",false))
			   ._script()
			   .button(onClick("buildGoogleMap('" +actionParameters.getUrl() +"', '" +actionParameters.getTopic() +"', '" +actionParameters.getLatitudeName() +"', '" +actionParameters.getLongitudeName() +"', '" +actionParameters.getLabelName() +"')").style("btn btn-danger")).write("Load")._button()   
			   .div(id("container").style("min-width: 310px; height: 400px; margin: 0 auto"))._div()
			._div();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return canvas;
	}

	

}
