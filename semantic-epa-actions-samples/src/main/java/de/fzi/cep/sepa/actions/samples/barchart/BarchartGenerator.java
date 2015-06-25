package de.fzi.cep.sepa.actions.samples.barchart;

import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.onClick;
import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.StringResource;

import de.fzi.cep.sepa.actions.samples.HtmlGenerator;

public class BarchartGenerator extends HtmlGenerator<BarChartParameters>{

	public BarchartGenerator(BarChartParameters actionParameters) {
		super(actionParameters);
		// TODO Auto-generated constructor stub
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
			   		.render(new StringResource("bar/bar.js",false))
			   ._script()
			   .button(onClick("buildBarChart('" +actionParameters.getUrl() +"', '" +actionParameters.getTopic() +"', '" +actionParameters.getListPropertyName() +"', '" +actionParameters.getKeyName() +"', '" +actionParameters.getValueName() +"')").style("btn btn-danger")).write("Load")._button()   
			   .div(id("container").class_("epoch category10").style("min-width: 310px; height: 700px; margin: 0 auto"))
				   	.div(id("barChart").class_(" epoch category10"))	
				   	._div()
			   ._div().div(id("legend"))._div()
			._div();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return canvas;
	}

}
