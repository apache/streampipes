package org.streampipes.pe.sources.samples.friction;

import org.streampipes.commons.Utils;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.vocabulary.MessageFormat;
import org.streampipes.vocabulary.MhWirth;
import org.streampipes.vocabulary.SO;
import org.streampipes.pe.sources.samples.config.ProaSenseSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 26.10.2016.
 */
public class FrictionCoefficient  {

    protected final String SwivelTopic = "";
    protected final String GearboxTopic = "";

    private FrictionVariable variable;

    protected FrictionCoefficient(FrictionVariable variable) {
        this.variable = variable;
    }

    public EventStream prepareStream(SepDescription sep) {

        EventStream stream = new EventStream();
        stream.setName(variable.label());
        stream.setDescription(variable.description());
        stream.setUri(sep.getUri() + variable.path());

        EventSchema schema = new EventSchema();
        List<EventProperty> eventProperties = new ArrayList<EventProperty>();
        eventProperties.add(EpProperties.stringEp("timestamp", "http://schema.org/DateTime"));
        eventProperties.add(EpProperties.stringEp("eventId", SO.Text));
        eventProperties.add(EpProperties.doubleEp("zScore", MhWirth.zScore));
        eventProperties.add(EpProperties.doubleEp("value", MhWirth.FrictionValue));
        eventProperties.add(EpProperties.doubleEp("std", MhWirth.Stddev));

        schema.setEventProperties(eventProperties);
        stream.setEventSchema(schema);

        EventGrounding grounding = new EventGrounding();
        grounding.setTransportProtocol(ProaSenseSettings.standardProtocol(variable.topic()));
        grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));
        stream.setEventGrounding(grounding);

        return stream;
    }
}
