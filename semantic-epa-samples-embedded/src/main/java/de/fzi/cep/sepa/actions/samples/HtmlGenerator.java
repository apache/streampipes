package de.fzi.cep.sepa.actions.samples;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.ext.jquery.JQueryLibrary;


public abstract class HtmlGenerator<T extends ActionParameters> {

	protected T actionParameters;
	
	public HtmlGenerator(T actionParameters)
	{
		this.actionParameters = actionParameters;
	}
	
	public String generateHtml()
	{
		return buildHtmlCanvas().toHtml();	
	}
	
	protected abstract HtmlCanvas buildHtmlCanvas();
	
	public String generateHtmlBody()
	{
		HtmlCanvas header = new HtmlCanvas();
		HtmlCanvas footer = new HtmlCanvas();
		try {
			header.html()
			  .render(JQueryLibrary.core("1.6.4"))
			  .body();
			footer._body()._html();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		return header.toString() + buildHtmlCanvas().toHtml() + footer.toString();
	};
	
}
