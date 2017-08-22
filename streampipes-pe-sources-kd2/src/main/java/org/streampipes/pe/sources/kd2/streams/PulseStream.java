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
public class PulseStream extends AbstractBioDataStream {

    @Override
    public EventStream declareModel(SepDescription sep) {
        EventStream stream = prepareStream(Kd2Variables.Pulse.topic());

        EventSchema schema = new EventSchema();
        List<EventProperty> eventProperties = new ArrayList<EventProperty>();
        eventProperties.addAll(getPreparedProperties());
        eventProperties.add(BiodataUtils.getPulseProperty());

        schema.setEventProperties(eventProperties);
        stream.setEventSchema(schema);
        stream.setName(Kd2Variables.Pulse.eventName());
        stream.setDescription(Kd2Variables.Pulse.description());
        stream.setUri(sep.getUri() + "/pulse");

        return stream;
    }
}
