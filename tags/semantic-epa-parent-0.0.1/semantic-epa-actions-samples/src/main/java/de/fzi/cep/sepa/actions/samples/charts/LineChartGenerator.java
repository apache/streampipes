package de.fzi.cep.sepa.actions.samples.charts;

import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.onLoad;

import java.io.IOException;
import java.util.Arrays;

import org.rendersnake.HtmlCanvas;

import de.fzi.cep.sepa.actions.samples.HtmlGenerator;

public class LineChartGenerator extends HtmlGenerator<LineChartParameters>{

	
	public LineChartGenerator(LineChartParameters lineChart)
	{
		super(lineChart);
	}
	


	@Override
	protected HtmlCanvas buildHtmlCanvas() {
		HtmlCanvas canvas = new HtmlCanvas();
		try {
			canvas = getStandardizedHeader(canvas, Arrays.asList("highcharts.js", "exporting.js", "stomp.js", "linechart2.js"), Arrays.asList());
			canvas.body(onLoad("buildLineChart('" +actionParameters.getTitle() +"', '" +actionParameters.getUrl() +"', '" +actionParameters.getTopic() +"', '" +actionParameters.getVariableName() +"')"))   
			       .div(id("container").style("min-width: 310px; height: 400px; margin: 0 auto"))._div();
			canvas = getStandardizedFooter(canvas);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return canvas;
	}
}
