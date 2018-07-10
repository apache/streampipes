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

package org.streampipes.connect.firstconnector.pipeline.elements;

import org.streampipes.connect.firstconnector.pipeline.AdapterPipelineElement;
import org.streampipes.connect.firstconnector.transform.DeleteTransformationRule;
import org.streampipes.connect.firstconnector.transform.EventTransformer;
import org.streampipes.connect.firstconnector.transform.TransformationRule;
import org.streampipes.model.modelconnect.TransformationRuleDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TransformSchemaAdapterPipelineElement implements AdapterPipelineElement {

    private EventTransformer eventTransformer;

    public TransformSchemaAdapterPipelineElement(List<TransformationRuleDescription> transformationRuleDescriptions) {
        List<TransformationRule> rules = new ArrayList<>();

        for (TransformationRuleDescription ruleDescription : transformationRuleDescriptions) {
            String key = ruleDescription.getToDelete();
            rules.add(new DeleteTransformationRule(Arrays.asList(key)));
        }

        eventTransformer = new EventTransformer(rules);
    }

    @Override
    public Map<String, Object> process(Map<String, Object> event) {
        return eventTransformer.transform(event);
    }
}
