package de.fzi.cep.sepa.sources.kd2.streams;

import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sources.kd2.config.Kd2Variables;
import de.fzi.cep.sepa.sources.kd2.utils.BiodataUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 18.11.2016.
 */
public class BiodataStream extends AbstractBioDataStream {

    @Override
    public EventStream declareModel(SepDescription sep) {
        EventStream stream = prepareStream(Kd2Variables.Biodata.topic());

        EventSchema schema = new EventSchema();
        List<EventProperty> eventProperties = new ArrayList<EventProperty>();
        eventProperties.addAll(getPreparedProperties());
        eventProperties.add(BiodataUtils.getHeartRateProperty());
        eventProperties.add(BiodataUtils.getEdaProperty());
        eventProperties.add(BiodataUtils.getArousalProperty());
        eventProperties.add(BiodataUtils.getPulseProperty());

        schema.setEventProperties(eventProperties);
        stream.setEventSchema(schema);
        stream.setName(Kd2Variables.Biodata.eventName());
        stream.setDescription(Kd2Variables.Biodata.description());
        stream.setUri(sep.getUri() + "/biodata");

        return stream;
    }
}
