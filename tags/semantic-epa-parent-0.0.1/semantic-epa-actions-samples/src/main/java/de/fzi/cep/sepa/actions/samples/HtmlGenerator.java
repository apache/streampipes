package de.fzi.cep.sepa.actions.samples;

import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;
import java.util.List;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.StringResource;
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
	
	public HtmlCanvas getStandardizedHeader(HtmlCanvas canvas, List<String> jsResources, List<String> cssResources) throws IOException {
		canvas
				.html()
				.head()
					.macros().stylesheet("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css")
					.style(type("text/css"));
				
				for(String cssResource : cssResources) {
					canvas
						.render(new StringResource(cssResource));
				}
					
				canvas
					._style()
					.render(JQueryLibrary.core("2.1.3"))
					.script(type("text/javascript"));
				
				for(String jsResource : jsResources) {
					canvas
						.render(new StringResource(jsResource, false));
				}

				canvas
					._script()
					._head();
		return canvas;
	}
	
	public HtmlCanvas getStandardizedFooter(HtmlCanvas canvas) throws IOException {
		return canvas._body()._html();
	}
	
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
