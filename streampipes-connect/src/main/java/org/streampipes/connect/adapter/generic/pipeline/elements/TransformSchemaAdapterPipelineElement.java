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

package org.streampipes.connect.adapter.generic.pipeline.elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.adapter.generic.pipeline.AdapterPipelineElement;
import org.streampipes.connect.adapter.generic.transform.*;
import org.streampipes.model.connect.rules.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TransformSchemaAdapterPipelineElement implements AdapterPipelineElement {

    private EventTransformer eventTransformer;
    Logger logger = LoggerFactory.getLogger(TransformSchemaAdapterPipelineElement.class);

    public TransformSchemaAdapterPipelineElement(List<TransformationRuleDescription> transformationRuleDescriptions) {
        List<TransformationRule> rules = new ArrayList<>();

        // transforms description to actual rules
        for (TransformationRuleDescription ruleDescription : transformationRuleDescriptions) {
            if (ruleDescription instanceof RenameRuleDescription) {
                RenameRuleDescription tmp = (RenameRuleDescription) ruleDescription;
                rules.add(new RenameTransformationRule(toKeyArray(tmp.getOldRuntimeKey()), getLastKey(tmp.getNewRuntimeKey())));
            } else if (ruleDescription instanceof MoveRuleDescription) {
                MoveRuleDescription tmp = (MoveRuleDescription) ruleDescription;
                rules.add(new MoveTransformationRule(toKeyArray(tmp.getOldRuntimeKey()), toKeyArray(tmp.getNewRuntimeKey())));
            } else if (ruleDescription instanceof CreateNestedRuleDescription) {
                CreateNestedRuleDescription tmp = (CreateNestedRuleDescription) ruleDescription;
                rules.add(new CreateNestedTransformationRule(toKeyArray(tmp.getRuntimeKey())));
            } else if (ruleDescription instanceof DeleteRuleDescription) {
                DeleteRuleDescription tmp = (DeleteRuleDescription) ruleDescription;
                rules.add(new DeleteTransformationRule(toKeyArray(tmp.getRuntimeKey())));
            } else {
                logger.error("Could not find the class for the rule description. This should never happen. Talk to admins to extend the rule implementations to get rid of this error!");
            }
        }

        eventTransformer = new EventTransformer(rules);
    }

    private String getLastKey(String s) {
        String[] list = s.split("\\.");
        if (list.length == 0) {
            return s;
        } else {
            return list[list.length - 1];
        }
    }



    private List<String> toKeyArray(String s) {
        String[] split = s.split("\\.");
        if (split.length == 0) {
            return Arrays.asList(s);
        } else {
            return Arrays.asList(split);
        }
    }

    @Override
    public Map<String, Object> process(Map<String, Object> event) {
        return eventTransformer.transform(event);
    }
}
