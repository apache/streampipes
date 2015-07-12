package de.fzi.cep.sepa.sources.samples.taxi;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.commons.messaging.IMessagePublisher;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.sources.samples.activemq.ActiveMQPublisher;

public class NycTaxiUtils {

	public static EventSchema getEventSchema()
	{
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "medallion", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "hack_license", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "pickup_datetime", "", Utils.createURI("http://test.de/timestamp")));
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "dropoff_datetime", "", Utils.createURI("http://test.de/timestamp")));
		eventProperties.add(new EventPropertyPrimitive(XSD._integer.toString(), "trip_time_in_secs", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "trip_distance", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "pickup_longitude", "", Utils.createURI("http://test.de/longitude")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "pickup_latitude", "", Utils.createURI("http://test.de/latitude")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "dropoff_longitude", "", Utils.createURI("http://test.de/longitude")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "dropoff_latitude", "", Utils.createURI("http://test.de/latitude")));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "payment_type", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "fare_amount", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "surcharge", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "mta_tax", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "tip_amount", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "tolls_amount", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "total_amount", "", Utils.createURI("http://schema.org/Number")));

		//current time for later delay calculation
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "read_datetime", "", Utils.createURI("http://schema.org/Number")));

		return new EventSchema(eventProperties);
	}
	
	public static IMessagePublisher streamPublisher(String topicName)
	{
		try {
			return new ActiveMQPublisher(Configuration.TCP_SERVER_URL +":" +Configuration.TCP_SERVER_PORT, topicName);
		} catch (JMSException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
}
