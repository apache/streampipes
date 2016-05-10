package de.fzi.cep.sepa.actions.samples.table;

import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.onClick;
import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.StringResource;

import de.fzi.cep.sepa.actions.samples.HtmlGenerator;

public class MultiRowTableGenerator extends HtmlGenerator<MultiRowTableParameters>{

	public MultiRowTableGenerator(MultiRowTableParameters actionParameters) {
		super(actionParameters);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected HtmlCanvas buildHtmlCanvas() {
		HtmlCanvas canvas = new HtmlCanvas();
		try {
			canvas.div()
					.script(type("text/javascript"))
					.render(new StringResource("stomp.js", false))
					.render(new StringResource("multirowdatatable.js", false))
					._script()
					.button(onClick(
							"buildTable('"
									+ actionParameters.getUrl()
									+ "', '"
									+ actionParameters.getTopic()
									+ "', '"
									+ actionParameters.getListProperty()
									+ "', '"
									+ actionParameters.isReplace()
									+ "', "
									+ toJavascriptArray(actionParameters
											.getColumnNames()) + ")").style(
							"btn btn-danger"))
					.write("Load")
					._button()
					.div(id("container").style(
							"min-width: 310px; height: 400px; margin: 0 auto"))
					._div()._div();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return canvas;
	}
	
	public static String toJavascriptArray(String[] arr) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < arr.length; i++) {
			sb.append("\'").append(arr[i]).append("\'");
			if (i + 1 < arr.length) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}

}
