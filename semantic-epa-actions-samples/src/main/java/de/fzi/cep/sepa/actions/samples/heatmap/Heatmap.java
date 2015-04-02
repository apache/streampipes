package de.fzi.cep.sepa.actions.samples.heatmap;

import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.onClick;
import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.StringResource;

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
			canvas.div()
			
			   .script(type("text/javascript"))
			   		.render(new StringResource("Queue.js",false))
					.render(new StringResource("stomp.js",false))
					.render(new StringResource("openlayers.js", false))
					.render(new StringResource("heatmap.js",false))
			   		.render(new StringResource("heatmap-openlayers.js",false))
			   		.render(new StringResource("heatmap-controller.js",false))
			   ._script()
			   .button(onClick("buildGoogleMap('" +actionParameters.getUrl() +"', '" +actionParameters.getTopic() +"', '" +actionParameters.getLatitudeName() +"', '" +actionParameters.getLongitudeName() +"', '" +actionParameters.getMaxPoints() +"')").style("btn btn-danger")).write("Load")._button()   
			   .div(id("container").style("min-width: 310px; height: 700px; margin: 0 auto"))._div()
			._div();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return canvas;
	}
	
}
