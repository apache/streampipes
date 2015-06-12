package de.fzi.cep.sepa.actions.samples.table;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.actions.config.ActionConfig;
import de.fzi.cep.sepa.actions.samples.ActionController;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyList;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.OneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.Option;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;

public class MultiRowTableController extends ActionController {

	@Override
	public SecDescription declareModel() {
		SecDescription sec = new SecDescription("/table/multirow", "Multi-Row Table", "", "");
		sec.setIconUrl(ActionConfig.iconBaseUrl + "/Table_Icon_HQ.png");
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		EventPropertyList e1 = new EventPropertyList();
		e1.setEventProperties(new ArrayList<>());
		eventProperties.add(e1);
		
		EventSchema schema1 = new EventSchema();
		schema1.setEventProperties(eventProperties);
		
		EventStream stream1 = new EventStream();
		stream1.setEventSchema(schema1);		
		stream1.setUri(ActionConfig.serverUrl +"/" +Utils.getRandomString());
		
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		OneOfStaticProperty operation = new OneOfStaticProperty("output", "Output strategy: ");
		operation.addOption(new Option("Replace rows"));
		operation.addOption(new Option("Append rows"));
		
		staticProperties.add(operation);
		staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementName()), "list", "Select list property"));
	
		sec.addEventStream(stream1);
		sec.setStaticProperties(staticProperties);
		
		return sec;
	}

	@Override
	public boolean invokeRuntime(SecInvocation sec) {
		
		return true;
	}

	@Override
	public boolean isVisualizable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getHtml(SecInvocation sec) {
		String[] propertyNames = new String[0];
		String newUrl = createWebsocketUri(sec);
		String inputTopic = extractTopic(sec);
		boolean replace = false;
		
		String outputStrategy = SepaUtils.getOneOfProperty(sec,
				"output");
		
		//if (outputStrategy.equals("replace")) replace = true;
		//else replace = false;
		
		String listProperty = SepaUtils.getMappingPropertyName(sec,
				"list");
		
		for(EventProperty p : sec.getInputStreams().get(0).getEventSchema().getEventProperties())
		{
			if (p.getRuntimeName().equals(listProperty))
			{
				if (p instanceof EventPropertyList) propertyNames = getColumnNames(((EventPropertyList) p).getEventProperties());
			}
		}
		
		MultiRowTableParameters tableParameters = new MultiRowTableParameters(inputTopic, newUrl, replace, listProperty, propertyNames);
		
		return new MultiRowTableGenerator(tableParameters).generateHtml();
	}

	@Override
	public boolean detachRuntime() {
		// TODO Auto-generated method stub
		return false;
	}



}
