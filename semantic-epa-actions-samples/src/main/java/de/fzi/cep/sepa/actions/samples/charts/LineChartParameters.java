package de.fzi.cep.sepa.actions.samples.charts;

import de.fzi.cep.sepa.actions.samples.ActionParameters;

public class LineChartParameters extends ActionParameters {

	private String title;
	private String xAxis;
	private String yAxis;
	
	
	private String variableName;
	
	public LineChartParameters(String title, String xAxis, String yAxis, String topic,
			String url, String variableName) {
		super(topic, url);
		this.title = title;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.variableName = variableName;
	}
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getxAxis() {
		return xAxis;
	}
	public void setxAxis(String xAxis) {
		this.xAxis = xAxis;
	}
	public String getyAxis() {
		return yAxis;
	}
	public void setyAxis(String yAxis) {
		this.yAxis = yAxis;
	}
	public String getVariableName() {
		return variableName;
	}
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	
	
}
