package org.streampipes.pe.sources.kd2.streams;

import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.pe.sources.kd2.config.Kd2Variables;
import org.streampipes.pe.sources.kd2.utils.BiodataUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 18.11.2016.
 */
public class HeartRateStream extends AbstractBioDataStream {
    @Override
    public EventStream declareModel(SepDescription sep) {
        EventStream stream = prepareStream(Kd2Variables.HeartRate.topic());

        EventSchema schema = new EventSchema();
        List<EventProperty> eventProperties = new ArrayList<EventProperty>();
        eventProperties.addAll(getPreparedProperties());
        eventProperties.add(BiodataUtils.getHeartRateProperty());

        schema.setEventProperties(eventProperties);
        stream.setEventSchema(schema);
        stream.setName(Kd2Variables.HeartRate.eventName());
        stream.setDescription(Kd2Variables.HeartRate.description());
        stream.setUri(sep.getUri() + "/heartrate");

        return stream;
    }
}
