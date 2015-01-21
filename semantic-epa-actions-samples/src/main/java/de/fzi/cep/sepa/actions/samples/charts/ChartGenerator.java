package de.fzi.cep.sepa.actions.samples.charts;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.StringResource;
import org.rendersnake.ext.jquery.JQueryLibrary;

import static org.rendersnake.HtmlAttributesFactory.*;

public class ChartGenerator {

	private LineChartParameters lineChart;
	
	public ChartGenerator(LineChartParameters lineChart)
	{
		this.lineChart = lineChart;
	}
	
	public String generateHtml()
	{
		HtmlCanvas html = new HtmlCanvas();
		try {
			html
			.div()
			   .script(type("text/javascript"))
			    .render(new StringResource("highcharts.js",false))
			     .render(new StringResource("exporting.js",false))
					   .render(new StringResource("stomp.js",false))
			   			.render(new StringResource("linechart2.js",false))
					   ._script()
					   .button(onClick("buildLineChart('" +lineChart.getTitle() +"', '" +lineChart.getUrl() +"', '" +lineChart.getTopic() +"', '" +lineChart.getVariableName() +"')").style("btn btn-danger")).write("Load")._button()   
			       .div(id("container").style("min-width: 310px; height: 400px; margin: 0 auto"))._div()
			       	  ._div();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return html.toHtml();
	}
	
	public String generateHtmlBody()
	{
		HtmlCanvas html = new HtmlCanvas();
		try {
			html
			  .html()
			  .render(JQueryLibrary.core("1.6.4"))
			  .script(src("http://code.highcharts.com/highcharts.js").type("text/javascript"))._script()
			   .script(src("http://code.highcharts.com/modules/exporting.js").type("text/javascript"))._script()
			   .script(type("text/javascript"))
			   			.render(new StringResource("stomp.js",false))
			   			.render(new StringResource("linechart2.js",false))		   
					   ._script()
			    .body(onLoad("buildLineChart('" +lineChart.getTitle() +"', '" +lineChart.getUrl() +"', '" +lineChart.getTopic() +"', '" +lineChart.getVariableName() +"')"))
			    .div(id("container").style("min-width: 310px; height: 400px; margin: 0 auto"))._div()
			    ._body()
			  ._html();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return html.toHtml();
	}
}
