package de.fzi.cep.sepa.sources.samples.proveit;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;

import de.fzi.cep.sepa.model.vocabulary.XSD;

import com.google.gson.Gson;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sources.samples.activemq.ActiveMQConsumer;
import de.fzi.cep.sepa.sources.samples.activemq.ActiveMQPublisher;
import de.fzi.cep.sepa.sources.samples.activemq.IMessageListener;
import de.fzi.cep.sepa.sources.samples.config.SampleSettings;
import de.fzi.proveit.senslet.model.Header;
import de.fzi.proveit.senslet.model.Senslet;
import de.fzi.proveit.senslet.model.input.BarcodeInput;
import de.fzi.proveit.senslet.model.input.CheckboxInput;
import de.fzi.proveit.senslet.model.input.ElementVisitor;
import de.fzi.proveit.senslet.model.input.GPSInput;
import de.fzi.proveit.senslet.model.input.PictureInput;
import de.fzi.proveit.senslet.model.input.RadioInput;
import de.fzi.proveit.senslet.model.input.SensletInputElement;
import de.fzi.proveit.senslet.model.input.SignatureInput;
import de.fzi.proveit.senslet.model.input.StaticElement;
import de.fzi.proveit.senslet.model.input.TextInput;
import de.fzi.proveit.senslet.model.property.ReportProperty;
import de.fzi.proveit.senslet.model.property.ReportPropertyType;
import de.fzi.proveit.senslet.model.property.SensletProperty;
import de.fzi.proveit.senslet.model.property.SensletPropertyType;

public class ProveITEventStream implements EventStreamDeclarer {

	private Senslet senslet;
	
	private String brokerUrl;
	private String sourceTopic;
	private String destinationTopic;
	
	public ProveITEventStream(Senslet senslet)
	{
		this.senslet = senslet;
		
	}

	@Override
	public EventStream declareModel(SepDescription sep) {
		
		EventStream stream = new EventStream();
		EventSchema schema = new EventSchema();
		
		schema.setEventProperties(makeEventProperties());
		stream.setEventSchema(schema);
		EventGrounding standardGrounding = standardGrounding(senslet.getHeader().getSensletType().toString());
		stream.setEventGrounding(standardGrounding);
		stream.setName(senslet.getHeader().getSensletType().toString());
		stream.setDescription(senslet.getHeader().getSensletType().toString());
		stream.setUri(sep.getUri() + "/" +senslet.getHeader().getSensletType().toString());
		
		int port = ((JmsTransportProtocol) standardGrounding.getTransportProtocol()).getPort();
		this.brokerUrl = standardGrounding.getTransportProtocol().getBrokerHostname() + ":" +port;
		this.destinationTopic = standardGrounding.getTransportProtocol().getTopicName();
		this.sourceTopic = "ProveIT.*." +senslet.getHeader().getSensletType().toString();
		
		return stream;
	}

	private List<EventProperty> makeEventProperties() {
		List<EventProperty> properties = new ArrayList<>();
		
		properties.addAll(sensletPropertiesToEventProperty(senslet.getSensletProperties()));
		properties.addAll(reportPropertiesToEventProperty(senslet.getReportProperties()));
		properties.addAll(sensletElementsToEventProperties(senslet.getSensletElements()));
		return properties;
	}

	private List<EventProperty> sensletPropertiesToEventProperty(
			Map<SensletPropertyType, SensletProperty> sensletProperties) {
		List<EventProperty> properties = new ArrayList<>();
		for(SensletPropertyType type : sensletProperties.keySet())
		{
			properties.add(convertSensletProperty(sensletProperties.get(type)));
		}
		return properties;
	}

	private EventProperty convertSensletProperty(SensletProperty sensletProperty) {
		if (sensletProperty.getDataType().equals("java.lang.String"))
			return new EventPropertyPrimitive(XSD._string.toString(), sensletProperty.getSensletPropertyType().toString(), "", toUri("http://schema.org/name"));
		else
			return new EventPropertyPrimitive(XSD._long.toString(), sensletProperty.getSensletPropertyType().toString(), "", toUri("http://schema.org/number"));
	}

	private List<EventProperty> reportPropertiesToEventProperty(
			Map<ReportPropertyType, ReportProperty> reportProperties) {
		List<EventProperty> properties = new ArrayList<>();
		for(ReportPropertyType type : reportProperties.keySet())
		{
			properties.add(convertReportProperty(reportProperties.get(type)));
		}
		return properties;
	}

	private EventProperty convertReportProperty(ReportProperty reportProperty) {
		if (reportProperty.getDataType().equals("java.lang.long"))
			return new EventPropertyPrimitive(XSD._long.toString(), reportProperty.getRpt().toString(), "", toUri("http://schema.org/number"));
		else
			return new EventPropertyPrimitive(XSD._string.toString(), reportProperty.getRpt().toString(), "", toUri("http://schema.org/name"));
	}

	
	private List<EventProperty> sensletElementsToEventProperties(
			List<SensletInputElement> sensletElements) {
		
		List<EventProperty> properties = new ArrayList<>();
		
		ElementVisitor visitor = new ElementVisitor() {
			
			@Override
			public void visit(SensletProperty sensletProperty) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(ReportProperty reportProperty) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(TextInput textInput) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(StaticElement staticElement) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(SignatureInput signatureInput) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(RadioInput radioInput) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(PictureInput pictureInput) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(GPSInput gpsInput) {
				EventPropertyNested nested = new EventPropertyNested("location");
				List<EventProperty> prop = new ArrayList<EventProperty>();
				
				prop.add(new EventPropertyPrimitive(XSD._double.toString(), "latitude", "", Utils.createURI("http://test.de/longitude")));
				prop.add(new EventPropertyPrimitive(XSD._double.toString(), "longitude", "", Utils.createURI("http://test.de/latitude")));
				
				nested.setEventProperties(prop);
				properties.add(nested);
			}
			
			@Override
			public void visit(CheckboxInput checkboxInput) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void visit(BarcodeInput barcodeInput) {
				// TODO Auto-generated method stub
				
			}
		};
		
		for(SensletInputElement element : sensletElements)
		{
			element.accept(visitor);
		}
		
		return properties;
		
	}

	@Override
	public void executeStream() {
		
		Runnable r = new Runnable() {

			@Override
			public void run() {
				
				try {
					ActiveMQPublisher publisher = new ActiveMQPublisher(brokerUrl, destinationTopic);
					ActiveMQConsumer consumer = new ActiveMQConsumer(brokerUrl, sourceTopic);
					ProveITEventSender sender = new ProveITEventSender(publisher);
					consumer.setListener(sender);
				} catch (JMSException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
			}
			
		};
		
		Thread thread = new Thread(r);
		thread.start();
		
	}

	@Override
	public boolean isExecutable() {
		return true;
	}
	
	private EventGrounding standardGrounding(String eventName)
	{
		EventGrounding grounding = new EventGrounding();
		grounding.setTransportProtocol(SampleSettings.jmsProtocol("SEP.Flattened." +eventName));
		return grounding;
	}
	
	private List<URI> toUri(String uri)
	{
		return de.fzi.cep.sepa.commons.Utils.createList(URI.create(uri));
	}
}
