package de.fzi.cep.sepa.sources.kd2.streams;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.builder.EpProperties;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;
import de.fzi.cep.sepa.sources.AbstractAlreadyExistingStream;
import de.fzi.cep.sepa.sources.kd2.config.KafkaSettings;
import de.fzi.cep.sepa.sources.kd2.vocabulary.Kd2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 18.11.2016.
 */
public abstract class AbstractBioDataStream extends AbstractAlreadyExistingStream {

    public EventStream prepareStream(String topic) {

        EventStream stream = new EventStream();

        EventGrounding grounding = new EventGrounding();
        grounding.setTransportProtocol(KafkaSettings.standardProtocol(topic));
        grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));

        stream.setEventGrounding(grounding);

        return stream;
    }

    public List<EventProperty> getPreparedProperties() {
        List<EventProperty> eventProperties = new ArrayList<>();
        eventProperties.add(EpProperties.longEp("receiveTime", Utils.createURI("http://schema.org/DateTime")));
        eventProperties.add(EpProperties.longEp("sampleTime", Utils.createURI("http://schema.org/DateTime")));
        eventProperties.add(EpProperties.integerEp("plugSeqNo", Kd2.plugSeqNo));
        eventProperties.add(EpProperties.stringEp("clientId", Kd2.clientId));

        return eventProperties;

    }
}
