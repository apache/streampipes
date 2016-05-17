package de.fzi.cep.sepa.html;

import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.href;
import static org.rendersnake.HtmlAttributesFactory.name;

import java.io.IOException;
import java.util.List;

import org.rendersnake.HtmlCanvas;

import de.fzi.cep.sepa.html.model.AgentDescription;
import de.fzi.cep.sepa.html.model.Description;
import de.fzi.cep.sepa.html.model.StreamDescription;

public class HTMLGenerator {

	private List<Description> description;
	
	public HTMLGenerator(List<Description> description)
	{
		this.description = description;
	}
	
	public String buildHtml()
	 {
		 HtmlCanvas html = new HtmlCanvas();
			try {
				html
					.head()
					.meta(name("viewport").content("width=device-width, initial-scale=1"))
					.macros().javascript("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js")
					.macros().stylesheet("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css")
					.macros().javascript("https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js")
					.style().write("body {padding-top: 70px;}")._style()
					._head()
					.body()
					.nav(class_("navbar navbar-inverse navbar-fixed-top"))
						.div(class_("container"))
						.div(class_("navbar-header"))
						.a(class_("navbar-brand"))
							.content("Semantic-EPA")
						._div()
						._div()
					._nav()
					.div(class_("container"));
						
				html.h4().write("Copy and paste the following URIs into the pipeline editor to add all elements of this node.")._h4();
				html.h5();
				for(Description description : description)
				{
					html.write(description.getUri().toString() +" ");
				}
				html._h5();
				
				for(Description description : description)
				{
					
					html.h3();
					html.write(description.getName());
					html._h3();
					html.h4().write("URI: " ).a(href(description.getUri().toString())).content(description.getUri().toString())._h4();
					html.h4().write("Description: ").write(description.getDescription())._h4();
					if (description instanceof StreamDescription)
					{
						StreamDescription streamDescription = (StreamDescription) description;
						for(AgentDescription agentDesc : streamDescription.getStreams())
						{
							html.h5().write(agentDesc.getName())._h5();
							html.h5().write("URI: ").a(href(agentDesc.getUri().toString())).content(agentDesc.getUri().toString())._h5();
							html.h5().write(agentDesc.getDescription())._h5();
						
						}			
					}
				}
				html._div();
				html._body();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(html.toHtml());
			return html.toHtml();
	 }
}
