package de.fzi.cep.sepa.actions.samples.maps;

import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.onLoad;

import java.io.IOException;
import java.util.Arrays;

import org.rendersnake.HtmlCanvas;

import de.fzi.cep.sepa.actions.samples.HtmlGenerator;

public class MapsGenerator extends HtmlGenerator<MapsParameters>{

	public MapsGenerator(MapsParameters actionParameters) {
		super(actionParameters);
	}

	@Override
	protected HtmlCanvas buildHtmlCanvas() {
		HtmlCanvas canvas = new HtmlCanvas();
		try {
			canvas = getStandardizedHeader(canvas, Arrays.asList("stomp.js", "gMaps.js"), Arrays.asList());
			canvas.body(onLoad("buildGoogleMap('" +actionParameters.getUrl() +"', '" +actionParameters.getTopic() +"', '" +actionParameters.getLatitudeName() +"', '" +actionParameters.getLongitudeName() +"', '" +actionParameters.getLabelName() +"')"))   
			   .div(id("container").style("min-width: 310px; height: 700px; margin: 0 auto"))._div();
			canvas = getStandardizedFooter(canvas);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return canvas;
	}

	

}
