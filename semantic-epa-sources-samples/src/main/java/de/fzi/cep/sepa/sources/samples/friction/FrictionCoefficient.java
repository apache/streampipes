package de.fzi.cep.sepa.sources.samples.friction;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.builder.EpProperties;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.model.vocabulary.MhWirth;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.sources.samples.config.ProaSenseSettings;

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
