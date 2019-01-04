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
import org.streampipes.connect.adapter.generic.pipeline.elements.DuplicateFilter;
import org.streampipes.connect.adapter.generic.pipeline.elements.SendToKafkaAdapterSink;
import org.streampipes.connect.adapter.generic.pipeline.elements.TransformSchemaAdapterPipelineElement;
import org.streampipes.connect.adapter.generic.pipeline.elements.TransformValueAdapterPipelineElement;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.model.connect.rules.Stream.RemoveDuplicatesTransformationRuleDescription;
import org.streampipes.model.connect.rules.TransformationRuleDescription;

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
        // first transform schema before transforming vales
        // value rules should use unique keys for of new schema
        pipelineElements.add(new TransformSchemaAdapterPipelineElement(adapterDescription.getSchemaRules()));
        pipelineElements.add(new TransformValueAdapterPipelineElement(adapterDescription.getValueRules()));


        RemoveDuplicatesTransformationRuleDescription duplicatesTransformationRuleDescription = getRemoveDuplicateRule(adapterDescription);
        if (duplicatesTransformationRuleDescription != null) {
            pipelineElements.add(new DuplicateFilter(duplicatesTransformationRuleDescription.getFilterTimeWindow()));
        }

        // Needed when adapter is
        if (adapterDescription.getEventGrounding() != null && adapterDescription.getEventGrounding().getTransportProtocol() != null
        && adapterDescription.getEventGrounding().getTransportProtocol().getBrokerHostname() != null) {
            pipelineElements.add(new SendToKafkaAdapterSink( adapterDescription));
        }

       return new AdapterPipeline(pipelineElements);
    }

    private RemoveDuplicatesTransformationRuleDescription getRemoveDuplicateRule(T adapterDescription) {

        for (TransformationRuleDescription tr : adapterDescription.getRules()) {
            if (tr instanceof RemoveDuplicatesTransformationRuleDescription) {
                return (RemoveDuplicatesTransformationRuleDescription) tr;
            }
        }

        return null;
    }

    public boolean isDebug() {
        return debug;
    }
}
