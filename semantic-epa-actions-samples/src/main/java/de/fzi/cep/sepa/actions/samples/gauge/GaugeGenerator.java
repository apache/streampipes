package de.fzi.cep.sepa.actions.samples.gauge;

import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.onClick;
import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.StringResource;

import de.fzi.cep.sepa.actions.samples.HtmlGenerator;

public class GaugeGenerator extends HtmlGenerator<GaugeParameters> {

	public GaugeGenerator(GaugeParameters actionParameters) {
		super(actionParameters);
	}

	@Override
	protected HtmlCanvas buildHtmlCanvas() {
		HtmlCanvas canvas = new HtmlCanvas();
		try {
			canvas.div()
			.style(type("text/css"))
			.render(new StringResource("epoch.min.css"))
			._style()
			   .script(type("text/javascript"))
					.render(new StringResource("stomp.js",false))
			   		.render(new StringResource("d3.min.js",false))
			   		.render(new StringResource("epoch.min.js",false))
			   		.render(new StringResource("gauge.js",false))
			   ._script()
			   .button(onClick("buildGauge('" +actionParameters.getUrl() +"', '" +actionParameters.getTopic() +"', '" +actionParameters.getVariableName() +"', " +actionParameters.getMin() +", " +actionParameters.getMax() +")").style("btn btn-danger")).write("Load")._button()   
			   .div(id("container").style("min-width: 310px; height: 400px; margin: 0 auto"))
			   		.div(id("gaugeChart").class_("epoch gauge-large"))._div()
			   ._div()
			._div();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return canvas;
	}

}
