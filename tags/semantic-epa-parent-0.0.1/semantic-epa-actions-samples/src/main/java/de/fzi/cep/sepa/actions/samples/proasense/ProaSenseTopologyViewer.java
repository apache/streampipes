package de.fzi.cep.sepa.actions.samples.proasense;

import static org.rendersnake.HtmlAttributesFactory.id;
import static org.rendersnake.HtmlAttributesFactory.onClick;
import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.StringResource;

import de.fzi.cep.sepa.actions.samples.HtmlGenerator;

public class ProaSenseTopologyViewer extends HtmlGenerator<ProaSenseTopologyParameters>{

	private ProaSenseEventNotifier notifier;
	
	public ProaSenseTopologyViewer(ProaSenseTopologyParameters actionParameters, ProaSenseEventNotifier notifier) {
		super(actionParameters);
		this.notifier = notifier;
	}

	@Override
	protected HtmlCanvas buildHtmlCanvas() {
		HtmlCanvas canvas = new HtmlCanvas();
		try {
			canvas.div()
					.h1().write(notifier.getEventName())._h1().h2().write(notifier.getCounter())._h2()
			._div();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return canvas;
	}

}
