/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.streampipes.connect.adapter.generic.pipeline.AdapterPipeline;
import org.streampipes.connect.adapter.generic.pipeline.AdapterPipelineElement;
import org.streampipes.connect.adapter.generic.pipeline.elements.*;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.connect.rules.Stream.RemoveDuplicatesTransformationRuleDescription;
import org.streampipes.model.connect.rules.TransformationRuleDescription;
import org.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription;

import java.util.ArrayList;
import java.util.List;

public abstract class Adapter<T extends AdapterDescription> {
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

    public abstract GuessSchema getSchema(T adapterDescription) throws AdapterException;

    public abstract String getId();

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


        // Needed when adapter is
        if (adapterDescription.getEventGrounding() != null && adapterDescription.getEventGrounding().getTransportProtocol() != null
        && adapterDescription.getEventGrounding().getTransportProtocol().getBrokerHostname() != null) {
            pipelineElements.add(new SendToKafkaAdapterSink( adapterDescription));
        }

       return new AdapterPipeline(pipelineElements);
    }

    private RemoveDuplicatesTransformationRuleDescription getRemoveDuplicateRule(T adapterDescription) {
        return getRule(adapterDescription, RemoveDuplicatesTransformationRuleDescription.class);
    }

    private AddTimestampRuleDescription getTimestampRule(T adapterDescription) {
        return getRule(adapterDescription, AddTimestampRuleDescription.class);
    }

    private AddValueTransformationRuleDescription getAddValueRule(T adapterDescription) {
        return getRule(adapterDescription, AddValueTransformationRuleDescription.class);
    }


    private <G extends TransformationRuleDescription> G getRule(T adapterDescription, Class<G> type) {

        for (TransformationRuleDescription tr : adapterDescription.getRules()) {
            if (type.isInstance(tr)) {
                return type.cast(tr);
            }
        }

        return null;
    }

    public boolean isDebug() {
        return debug;
    }
}
