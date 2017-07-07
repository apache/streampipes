package org.streampipes.pe.sources.samples.taxi;

import org.streampipes.commons.Utils;
import org.streampipes.commons.config.ClientConfiguration;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.kafka.StreamPipesKafkaProducer;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.vocabulary.Geo;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.model.vocabulary.XSD;

import java.util.ArrayList;
import java.util.List;

public class NycTaxiUtils {

	public static EventSchema getEventSchema()
	{
		List<EventProperty> eventProperties = new ArrayList<EventProperty>();
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "medallion", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._string.toString(), "hack_license", "", Utils.createURI(SO.Text)));
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "pickup_datetime", "",
						Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._long.toString(), "dropoff_datetime",
						"", Utils.createURI("http://schema.org/DateTime")));
		eventProperties.add(new EventPropertyPrimitive(XSD._integer.toString(), "trip_time_in_secs", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "trip_distance", "", Utils.createURI("http://schema.org/Number")));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "pickup_longitude", "", Utils.createURI(Geo.lng)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "pickup_latitude", "", Utils.createURI(Geo.lat)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "dropoff_longitude", "", Utils.createURI(Geo.lng)));
		eventProperties.add(new EventPropertyPrimitive(XSD._double.toString(), "dropoff_latitude", "", Utils.createURI(Geo.lat)));
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
	
	public static EventProducer streamPublisher(String topicName)
	{
		
			return new StreamPipesKafkaProducer(ClientConfiguration.INSTANCE.getKafkaUrl(), topicName);
	}
	
}
