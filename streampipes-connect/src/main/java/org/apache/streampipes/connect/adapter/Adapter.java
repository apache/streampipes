/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.connect.adapter;

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.config.backend.SpProtocol;
import org.apache.streampipes.connect.adapter.preprocessing.elements.*;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.adapter.exception.ParseException;
import org.apache.streampipes.connect.adapter.model.Connector;
import org.apache.streampipes.connect.adapter.model.pipeline.AdapterPipeline;
import org.apache.streampipes.connect.adapter.model.pipeline.AdapterPipelineElement;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription;
import org.apache.streampipes.model.grounding.TransportProtocol;

import java.util.ArrayList;
import java.util.List;

public abstract class Adapter<T extends AdapterDescription> implements Connector {
    Logger logger = LoggerFactory.getLogger(Adapter.class);

    private boolean debug;

    protected AdapterPipeline adapterPipeline;

    protected T adapterDescription;

    public Adapter(T adapterDescription, boolean debug) {
        this.adapterDescription = adapterDescription;
        this.debug = debug;
        this.adapterPipeline = getAdapterPipeline(adapterDescription);
    }

    public Adapter(T adapterDescription) {
        this(adapterDescription, false);
    }

    public Adapter(boolean debug) {
        this.debug = debug;
    }

    public Adapter() {
        this(false);
    }

    public abstract T declareModel();

    // Decide which adapter to call
    public abstract void startAdapter() throws AdapterException;

    public abstract void stopAdapter() throws AdapterException;

    public abstract Adapter getInstance(T adapterDescription);

    public abstract GuessSchema getSchema(T adapterDescription) throws AdapterException, ParseException;

    public abstract String getId();

    public void changeEventGrounding(TransportProtocol transportProtocol) {

        if (transportProtocol instanceof JmsTransportProtocol) {
            SendToJmsAdapterSink sink = (SendToJmsAdapterSink) this.adapterPipeline.getPipelineSink();
            if ("true".equals(System.getenv("SP_DEBUG"))) {
                transportProtocol.setBrokerHostname("localhost");
                //((JmsTransportProtocol) transportProtocol).setPort(61616);
            }
            sink.changeTransportProtocol((JmsTransportProtocol) transportProtocol);
        }
        else if (transportProtocol instanceof KafkaTransportProtocol) {
            SendToKafkaAdapterSink sink = (SendToKafkaAdapterSink) this.adapterPipeline.getPipelineSink();
            if ("true".equals(System.getenv("SP_DEBUG"))) {
                transportProtocol.setBrokerHostname("localhost");
                ((KafkaTransportProtocol) transportProtocol).setKafkaPort(9094);
            }
            sink.changeTransportProtocol((KafkaTransportProtocol) transportProtocol);
        }
        else if (transportProtocol instanceof MqttTransportProtocol) {
            SendToMqttAdapterSink sink = (SendToMqttAdapterSink) this.adapterPipeline.getPipelineSink();
            if ("true".equals(System.getenv("SP_DEBUG"))) {
                transportProtocol.setBrokerHostname("localhost");
                //((MqttTransportProtocol) transportProtocol).setPort(1883);
            }
            sink.changeTransportProtocol((MqttTransportProtocol) transportProtocol);
        }
    }

    private AdapterPipeline getAdapterPipeline(T adapterDescription) {

        List<AdapterPipelineElement> pipelineElements = new ArrayList<>();

        // Must be before the schema transformations to ensure that user can move this event property
        AddTimestampRuleDescription timestampTransformationRuleDescription = getTimestampRule(adapterDescription);
        if (timestampTransformationRuleDescription != null) {
            pipelineElements.add(new AddTimestampPipelineElement(timestampTransformationRuleDescription.getRuntimeKey()));
        }

        AddValueTransformationRuleDescription valueTransformationRuleDescription = getAddValueRule(adapterDescription);
        if (valueTransformationRuleDescription != null) {
            pipelineElements.add(new AddValuePipelineElement(valueTransformationRuleDescription.getRuntimeKey(), valueTransformationRuleDescription.getStaticValue()));
        }


        // first transform schema before transforming vales
        // value rules should use unique keys for of new schema
        pipelineElements.add(new TransformSchemaAdapterPipelineElement(adapterDescription.getSchemaRules()));
        pipelineElements.add(new TransformValueAdapterPipelineElement(adapterDescription.getValueRules()));


        RemoveDuplicatesTransformationRuleDescription duplicatesTransformationRuleDescription = getRemoveDuplicateRule(adapterDescription);
        if (duplicatesTransformationRuleDescription != null) {
            pipelineElements.add(new DuplicateFilterPipelineElement(duplicatesTransformationRuleDescription.getFilterTimeWindow()));
        }

        TransformStreamAdapterElement transformStreamAdapterElement = new TransformStreamAdapterElement();
        EventRateTransformationRuleDescription eventRateTransformationRuleDescription = getEventRateTransformationRule(adapterDescription);
        if (eventRateTransformationRuleDescription != null) {
            transformStreamAdapterElement.addStreamTransformationRuleDescription(eventRateTransformationRuleDescription);
        }
        pipelineElements.add(transformStreamAdapterElement);

        // Needed when adapter is (
        if (adapterDescription.getEventGrounding() != null && adapterDescription.getEventGrounding().getTransportProtocol() != null
                && adapterDescription.getEventGrounding().getTransportProtocol().getBrokerHostname() != null) {
            return new AdapterPipeline(pipelineElements, getAdapterSink(adapterDescription));
        }

        return new AdapterPipeline(pipelineElements);
    }

    private SendToBrokerAdapterSink<?> getAdapterSink(AdapterDescription adapterDescription) {
        SpProtocol prioritizedProtocol =
                BackendConfig.INSTANCE.getMessagingSettings().getPrioritizedProtocols().get(0);

        if (GroundingService.isPrioritized(prioritizedProtocol, JmsTransportProtocol.class)) {
            return new SendToJmsAdapterSink(adapterDescription);
        }
        else if (GroundingService.isPrioritized(prioritizedProtocol, KafkaTransportProtocol.class)) {
            return new SendToKafkaAdapterSink(adapterDescription);
        }
        else {
            return new SendToMqttAdapterSink(adapterDescription);
        }
    }

    private RemoveDuplicatesTransformationRuleDescription getRemoveDuplicateRule(T adapterDescription) {
        return getRule(adapterDescription, RemoveDuplicatesTransformationRuleDescription.class);
    }

    private EventRateTransformationRuleDescription getEventRateTransformationRule(T adapterDescription) {
        return getRule(adapterDescription, EventRateTransformationRuleDescription.class);
    }

    private AddTimestampRuleDescription getTimestampRule(T adapterDescription) {
        return getRule(adapterDescription, AddTimestampRuleDescription.class);
    }

    private AddValueTransformationRuleDescription getAddValueRule(T adapterDescription) {
        return getRule(adapterDescription, AddValueTransformationRuleDescription.class);
    }


    private <G extends TransformationRuleDescription> G getRule(T adapterDescription, Class<G> type) {

        if (adapterDescription != null) {
            for (TransformationRuleDescription tr : adapterDescription.getRules()) {
                if (type.isInstance(tr)) {
                    return type.cast(tr);
                }
            }
        }

        return null;
    }

    public boolean isDebug() {
        return debug;
    }

}
