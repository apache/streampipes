package org.streampipes.pe.sources.kd2.streams;

import org.streampipes.commons.Utils;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.vocabulary.MessageFormat;
import org.streampipes.sources.AbstractAlreadyExistingStream;
import org.streampipes.pe.sources.kd2.config.KafkaSettings;
import org.streampipes.pe.sources.kd2.vocabulary.Kd2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 18.11.2016.
 */
public abstract class AbstractBioDataStream extends AbstractAlreadyExistingStream {

    public SpDataStream prepareStream(String topic) {

        SpDataStream stream = new SpDataStream();

        EventGrounding grounding = new EventGrounding();
        grounding.setTransportProtocol(KafkaSettings.standardProtocol(topic));
        grounding.setTransportFormats(Utils.createList(new TransportFormat(MessageFormat.Json)));

        stream.setEventGrounding(grounding);

        return stream;
    }

    public List<EventProperty> getPreparedProperties() {
        List<EventProperty> eventProperties = new ArrayList<>();
        eventProperties.add(EpProperties.longEp(Labels.empty(), "receiveTime", Utils.createURI("http://schema.org/DateTime")));
        eventProperties.add(EpProperties.longEp(Labels.empty(), "sampleTime", Utils.createURI("http://schema.org/DateTime")));
        eventProperties.add(EpProperties.integerEp(Labels.empty(), "plugSeqNo", Kd2.plugSeqNo));
        eventProperties.add(EpProperties.stringEp(Labels.empty(), "clientId", Kd2.clientId));

        return eventProperties;

    }
}
