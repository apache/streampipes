package de.fzi.cep.sepa.actions.samples.file;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.actions.samples.ActionController;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;

public class FileController extends ActionController {

	@Override
	public SecDescription declareModel() {
		
		SecDescription sec = new SecDescription("/file", "File Output", "", "");
		
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		FreeTextStaticProperty maxNumberOfRows = new FreeTextStaticProperty("path", "Path");
		staticProperties.add(maxNumberOfRows);

		sec.addEventStream(stream1);
		sec.setStaticProperties(staticProperties);
		
		return sec;
	}

	@Override
	public Response invokeRuntime(SecInvocation sec) {
		String brokerUrl = createJmsUri(sec);
		String inputTopic = sec.getInputStreams().get(0).getEventGrounding().getTransportProtocol().getTopicName();
		
		String path = ((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByName(sec, "path"))).getValue();
		
		FileParameters fileParameters = new FileParameters(inputTopic, brokerUrl, path);
		
		new Thread(new FileWriter(fileParameters)).start();
		
		return null;
	}

	@Override
	public Response detachRuntime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVisualizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHtml(SecInvocation invocation) {
		// TODO Auto-generated method stub
		return null;
	}

}
