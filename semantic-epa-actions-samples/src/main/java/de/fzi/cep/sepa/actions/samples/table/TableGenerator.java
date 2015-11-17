package de.fzi.cep.sepa.actions.samples.table;

import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.onLoad;

import java.io.IOException;
import java.util.Arrays;

import org.rendersnake.HtmlCanvas;

import de.fzi.cep.sepa.actions.samples.HtmlGenerator;

public class TableGenerator extends HtmlGenerator<TableParameters> {

	public TableGenerator(TableParameters actionParameters) {
		super(actionParameters);
	}

	@Override
	protected HtmlCanvas buildHtmlCanvas() {
		
		HtmlCanvas canvas = new HtmlCanvas();
		try {
			canvas = getStandardizedHeader(canvas, Arrays.asList("stomp.js", "datatable.js"), Arrays.asList());
			canvas.body(onLoad(
							"buildTable('"
									+ actionParameters.getUrl()
									+ "', '"
									+ actionParameters.getTopic()
									+ "', '"
									+ actionParameters.getNumberOfRows()
									+ "', "
									+ toJavascriptArray(actionParameters
											.getColumnNames()) + ")").style(
							"btn btn-danger"))
					.div(id("container").style(
							"min-width: 310px; height: 400px; margin: 0 auto"))
					._div();
			return getStandardizedFooter(canvas);
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
