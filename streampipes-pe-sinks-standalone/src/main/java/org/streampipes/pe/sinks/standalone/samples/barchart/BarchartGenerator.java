package org.streampipes.pe.sinks.standalone.samples.barchart;

import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.onLoad;

import java.io.IOException;
import java.util.Arrays;

import org.rendersnake.HtmlCanvas;

import org.streampipes.pe.sinks.standalone.samples.HtmlGenerator;

public class BarchartGenerator extends HtmlGenerator<BarChartParameters>{

	public BarchartGenerator(BarChartParameters actionParameters) {
		super(actionParameters);
	}

	@Override
	protected HtmlCanvas buildHtmlCanvas() {
		HtmlCanvas canvas = new HtmlCanvas();
		try {
			canvas = getStandardizedHeader(canvas, Arrays.asList("stomp.js", "d3.min.js", "epoch.min.js", "bar/bar.js"), Arrays.asList("epoch.min.css"));
			canvas.body(onLoad("buildBarChart('" +actionParameters.getUrl() +"', '" +actionParameters.getTopic() +"', '" +actionParameters.getListPropertyName() +"', '" +actionParameters.getKeyName() +"', '" +actionParameters.getValueName() +"')"))   
			   .div(id("container").class_("epoch category10").style("min-width: 310px; height: 700px; margin: 0 auto"))
				   	.div(id("barChart").class_(" epoch category10"))	
				   	._div()
			   ._div().div(id("legend"))._div();
			canvas = getStandardizedFooter(canvas);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return canvas;
	}

}
