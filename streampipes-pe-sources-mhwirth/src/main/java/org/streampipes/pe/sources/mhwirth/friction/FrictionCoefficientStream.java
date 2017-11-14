package org.streampipes.pe.sources.mhwirth.friction;

import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.vocabulary.MessageFormat;
import org.streampipes.vocabulary.MhWirth;
import org.streampipes.vocabulary.SO;
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

    protected SpDataStream getPreparedEventStream(DataSourceDescription sep, AkerVariables variable, String pathName, String topic) {
        SpDataStream stream = new SpDataStream();

        EventSchema schema = new EventSchema();
        List<EventProperty> eventProperties = new ArrayList<EventProperty>();
        eventProperties.add(EpProperties.stringEp(Labels.empty(), "timestamp", "http://schema.org/DateTime"));
        eventProperties.add(EpProperties.stringEp(Labels.empty(), "eventId", SO.Text));
        eventProperties.add(EpProperties.doubleEp(Labels.empty(), "zScore", MhWirth.zScore));
        eventProperties.add(EpProperties.doubleEp(Labels.empty(), "value", MhWirth.FrictionValue));
        eventProperties.add(EpProperties.doubleEp(Labels.empty(), "std", MhWirth.Stddev));

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
