package org.streampipes.pe.sources.kd2.streams;

import org.streampipes.commons.Utils;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.vocabulary.MessageFormat;
import org.streampipes.sources.AbstractAlreadyExistingStream;
import org.streampipes.pe.sources.kd2.config.KafkaSettings;
import org.streampipes.pe.sources.kd2.vocabulary.Kd2;

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
