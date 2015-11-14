package de.fzi.cep.sepa.actions.samples.table;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.actions.samples.ActionController;
import de.fzi.cep.sepa.actions.samples.util.ActionUtils;
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

public class TableViewController extends ActionController {

	@Override
	public SecDescription declareModel() {
		SecDescription sec = new SecDescription("table", "Table", "", "");
		sec.setIconUrl(ActionConfig.iconBaseUrl + "/Table_Icon_HQ.png");
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
		FreeTextStaticProperty maxNumberOfRows = new FreeTextStaticProperty("rows", "Maximum number of rows", "");
		staticProperties.add(maxNumberOfRows);

		sec.addEventStream(stream1);
		sec.setStaticProperties(staticProperties);
		sec.setSupportedGrounding(ActionUtils.getSupportedGrounding());
		
		return sec;
	}

	@Override
	public boolean isVisualizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHtml(SecInvocation sec) {
		String newUrl = createWebsocketUri(sec);
		String inputTopic = extractTopic(sec);
		
		String rows = ((FreeTextStaticProperty) (SepaUtils
				.getStaticPropertyByInternalName(sec, "rows"))).getValue();
		
		TableParameters tableParameters = new TableParameters(inputTopic, newUrl, Integer.parseInt(rows), getColumnNames(sec.getInputStreams().get(0).getEventSchema().getEventProperties()));
		
		return new TableGenerator(tableParameters).generateHtml();
	}

	@Override
	public Response invokeRuntime(SecInvocation invocationGraph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response detachRuntime(String pipelineId) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
