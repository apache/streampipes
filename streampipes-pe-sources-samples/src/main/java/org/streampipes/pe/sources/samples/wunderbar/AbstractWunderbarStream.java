package org.streampipes.pe.sources.samples.wunderbar;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.streampipes.commons.Utils;
import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.quality.MeasurementCapability;
import org.streampipes.model.quality.MeasurementObject;
import org.streampipes.vocabulary.MessageFormat;
import org.streampipes.vocabulary.XSD;
import org.streampipes.pe.sources.samples.config.ProaSenseSettings;

public abstract class AbstractWunderbarStream implements DataStreamDeclarer {

	protected WunderbarVariables variable;
	
	protected final static String SEPA_PREFIX = "http://event-processing.org/sepa/";
	
	public AbstractWunderbarStream(WunderbarVariables variable) {
		this.variable = variable;
	}
	
	public SpDataStream prepareStream(DataSourceDescription sep) {
		
		SpDataStream stream = new SpDataStream();
		stream.setName(variable.eventName());
		stream.setDescription(variable.description());
		stream.setUri(sep.getUri() + variable.path());
		
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(ProaSenseSettings.jmsProtocol(variable.topic()));
		grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));
				
		stream.setEventGrounding(grounding);	
		
		return stream;
	}
	
	protected List<MeasurementCapability> mc(String capabilitySuffix) {
		return Arrays.asList(new MeasurementCapability(URI.create(SEPA_PREFIX +capabilitySuffix)));
	}
	
	protected List<MeasurementObject> mo(String measurementObjectSuffix) {
		return Arrays.asList(new MeasurementObject(URI.create(SEPA_PREFIX +measurementObjectSuffix)));
	}
	
	@Override
	public void executeStream() {
		
	}

	@Override
	public boolean isExecutable() {
		return false;
	}

	
	public EventPropertyPrimitive timestampProperty() {
		return new EventPropertyPrimitive(XSD._long.toString(), "timestamp", "", Utils.createURI("http://schema.org/DateTime"));
	}
}
