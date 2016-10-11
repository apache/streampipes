package de.fzi.cep.sepa.sources.mhwirth.friction;

import de.fzi.cep.sepa.model.builder.EpProperties;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.sources.AbstractAlreadyExistingStream;
import de.fzi.cep.sepa.sources.mhwirth.config.AkerVariables;
import de.fzi.cep.sepa.sources.mhwirth.config.ProaSenseSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 11.10.2016.
 */
public class FrictionCoefficientStream extends AbstractAlreadyExistingStream {

    @Override
    public EventStream declareModel(SepDescription sep) {

        EventStream stream = new EventStream();

        EventSchema schema = new EventSchema();
        List<EventProperty> eventProperties = new ArrayList<EventProperty>();
        eventProperties.add(EpProperties.stringEp("time", SO.DateCreated));
        eventProperties.add(EpProperties.stringEp("eventId", SO.Text));
        eventProperties.add(EpProperties.doubleEp("zScore", "http://mhwirth.com/zScore"));
        eventProperties.add(EpProperties.doubleEp("value", "http://mhwirth.com/frictionValue"));

        EventGrounding grounding = new EventGrounding();
        grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(AkerVariables.Friction.topic()));
        grounding.setTransportFormats(de.fzi.cep.sepa.commons.Utils.createList(new TransportFormat(MessageFormat.Json)));

        stream.setEventGrounding(grounding);
        schema.setEventProperties(eventProperties);
        stream.setEventSchema(schema);
        stream.setName(AkerVariables.Friction.eventName());
        stream.setDescription(AkerVariables.Friction.description());
        stream.setUri(sep.getUri() + "/friction");

        return stream;
    }

}
