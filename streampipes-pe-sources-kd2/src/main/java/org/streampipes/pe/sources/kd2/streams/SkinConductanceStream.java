package org.streampipes.pe.sources.kd2.streams;

import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.pe.sources.kd2.config.Kd2Variables;
import org.streampipes.pe.sources.kd2.utils.BiodataUtils;

import java.util.ArrayList;
import java.util.List;

public class SkinConductanceStream extends AbstractBioDataStream {

    @Override
    public SpDataStream declareModel(DataSourceDescription sep) {
        SpDataStream stream = prepareStream(Kd2Variables.SkinConductance.topic());

        EventSchema schema = new EventSchema();
        List<EventProperty> eventProperties = new ArrayList<EventProperty>();
        eventProperties.addAll(getPreparedProperties());
        eventProperties.add(BiodataUtils.getEdaProperty());

        schema.setEventProperties(eventProperties);
        stream.setEventSchema(schema);
        stream.setName(Kd2Variables.SkinConductance.eventName());
        stream.setDescription(Kd2Variables.SkinConductance.description());
        stream.setUri(sep.getUri() + "/skinconductance");

        return stream;
    }
}
