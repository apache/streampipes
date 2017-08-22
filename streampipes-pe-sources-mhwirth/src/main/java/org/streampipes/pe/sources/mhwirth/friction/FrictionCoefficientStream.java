package org.streampipes.pe.sources.mhwirth.friction;

import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.model.vocabulary.MessageFormat;
import org.streampipes.model.vocabulary.MhWirth;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.sources.AbstractAlreadyExistingStream;
import org.streampipes.pe.sources.mhwirth.config.AkerVariables;
import org.streampipes.pe.sources.mhwirth.config.ProaSenseSettings;
import org.streampipes.commons.Utils;

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
        grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));

        stream.setEventGrounding(grounding);
        schema.setEventProperties(eventProperties);
        stream.setEventSchema(schema);
        stream.setName(variable.eventName());
        stream.setDescription(variable.description());
        stream.setUri(sep.getUri() + pathName);

        return stream;
    }
}
