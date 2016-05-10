package de.fzi.cep.sepa.actions.samples.heatmap;

import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.onLoad;

import java.io.IOException;
import java.util.Arrays;

import org.rendersnake.HtmlCanvas;

import de.fzi.cep.sepa.actions.samples.HtmlGenerator;

public class Heatmap extends HtmlGenerator<HeatmapParameters> {

	public Heatmap(HeatmapParameters actionParameters) {
		super(actionParameters);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected HtmlCanvas buildHtmlCanvas() {
		HtmlCanvas canvas = new HtmlCanvas();
		try {
			canvas = getStandardizedHeader(canvas, Arrays.asList("Queue.js", "stomp.js", "openlayers.js", "heatmap.js", "heatmap-openlayers.js", "heatmap-controller.js"), Arrays.asList());
			canvas
				.body(onLoad("buildGoogleMap('" +actionParameters.getUrl() +"', '" +actionParameters.getTopic() +"', '" +actionParameters.getLatitudeName() +"', '" +actionParameters.getLongitudeName() +"', '" +actionParameters.getMaxPoints() +"')"))
			   .div(id("container").style("min-width: 310px; height: 700px; margin: 0 auto"))._div();
			canvas = getStandardizedFooter(canvas);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return canvas;
	}
	
}
