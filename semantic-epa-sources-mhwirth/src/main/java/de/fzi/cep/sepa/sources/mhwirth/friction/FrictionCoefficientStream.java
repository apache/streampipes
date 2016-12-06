package de.fzi.cep.sepa.sources.mhwirth.friction;

import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.MhWirth;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.sources.AbstractAlreadyExistingStream;
import de.fzi.cep.sepa.sources.mhwirth.config.AkerVariables;
import de.fzi.cep.sepa.sources.mhwirth.config.ProaSenseSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 12.10.2016.
 */
public abstract class FrictionCoefficientStream extends AbstractAlreadyExistingStream {

    protected EventStream getPreparedEventStream(SepDescription sep, AkerVariables variable, String pathName, String topic) {
        EventStream stream = new EventStream();

        EventSchema schema = new EventSchema();
        List<EventProperty> eventProperties = new ArrayList<EventProperty>();
        eventProperties.add(EpProperties.stringEp("timestamp", "http://schema.org/DateTime"));
        eventProperties.add(EpProperties.stringEp("eventId", SO.Text));
        eventProperties.add(EpProperties.doubleEp("zScore", MhWirth.zScore));
        eventProperties.add(EpProperties.doubleEp("value", MhWirth.FrictionValue));
        eventProperties.add(EpProperties.doubleEp("std", MhWirth.Stddev));

        EventGrounding grounding = new EventGrounding();
        grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(topic));
        grounding.setTransportFormats(de.fzi.cep.sepa.commons.Utils.createList(new TransportFormat(MessageFormat.Json)));

        stream.setEventGrounding(grounding);
        schema.setEventProperties(eventProperties);
        stream.setEventSchema(schema);
        stream.setName(variable.eventName());
        stream.setDescription(variable.description());
        stream.setUri(sep.getUri() + pathName);

        return stream;
    }
}
