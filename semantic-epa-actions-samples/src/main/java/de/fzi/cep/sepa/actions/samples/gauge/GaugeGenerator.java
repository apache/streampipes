package de.fzi.cep.sepa.actions.samples.gauge;

import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.onLoad;

import java.io.IOException;
import java.util.Arrays;

import org.rendersnake.HtmlCanvas;

import de.fzi.cep.sepa.actions.samples.HtmlGenerator;

public class GaugeGenerator extends HtmlGenerator<GaugeParameters> {

	public GaugeGenerator(GaugeParameters actionParameters) {
		super(actionParameters);
	}

	@Override
	protected HtmlCanvas buildHtmlCanvas() {
		HtmlCanvas canvas = new HtmlCanvas();
		try {
			canvas = getStandardizedHeader(canvas, Arrays.asList("stomp.js", "d3.min.js", "epoch.min.js", "gauge.js"), Arrays.asList("epoch.min.css"));
			canvas
				.body(onLoad("buildGauge('" +actionParameters.getUrl() +"', '" +actionParameters.getTopic() +"', '" +actionParameters.getVariableName() +"', " +actionParameters.getMin() +", " +actionParameters.getMax() +")"))
			   .div(id("container").style("width: 100%; height: 200px;"))
			   		.div(id("gaugeChart").class_("epoch gauge-medium"))._div()._div();
			  
			canvas = getStandardizedFooter(canvas);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return canvas;
	}

}
