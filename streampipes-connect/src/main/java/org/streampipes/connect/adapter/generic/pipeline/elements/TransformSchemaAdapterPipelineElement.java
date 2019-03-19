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
import org.streampipes.connect.adapter.generic.pipeline.Util;
import org.streampipes.connect.adapter.generic.transform.*;
import org.streampipes.connect.adapter.generic.transform.schema.*;
import org.streampipes.model.connect.rules.*;
import org.streampipes.model.connect.rules.Schema.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransformSchemaAdapterPipelineElement implements AdapterPipelineElement {

    private SchemaEventTransformer eventTransformer;
    Logger logger = LoggerFactory.getLogger(TransformSchemaAdapterPipelineElement.class);

    public TransformSchemaAdapterPipelineElement(List<SchemaTransformationRuleDescription> transformationRuleDescriptions) {
        List<TransformationRule> rules = new ArrayList<>();

        // transforms description to actual rules
        for (TransformationRuleDescription ruleDescription : transformationRuleDescriptions) {
            if (ruleDescription instanceof RenameRuleDescription) {
                RenameRuleDescription tmp = (RenameRuleDescription) ruleDescription;
                rules.add(new RenameTransformationRule(Util.toKeyArray(tmp.getOldRuntimeKey()), Util.getLastKey(tmp.getNewRuntimeKey())));
            } else if (ruleDescription instanceof MoveRuleDescription) {
                MoveRuleDescription tmp = (MoveRuleDescription) ruleDescription;
                rules.add(new MoveTransformationRule(Util.toKeyArray(tmp.getOldRuntimeKey()), Util.toKeyArray(tmp.getNewRuntimeKey())));
            } else if (ruleDescription instanceof CreateNestedRuleDescription) {
                CreateNestedRuleDescription tmp = (CreateNestedRuleDescription) ruleDescription;
                rules.add(new CreateNestedTransformationRule(Util.toKeyArray(tmp.getRuntimeKey())));
            } else if (ruleDescription instanceof DeleteRuleDescription) {
                DeleteRuleDescription tmp = (DeleteRuleDescription) ruleDescription;
                rules.add(new DeleteTransformationRule(Util.toKeyArray(tmp.getRuntimeKey())));
             } else {
                logger.error("Could not find the class for the rule description. This should never happen. Talk to admins to extend the rule implementations to get rid of this error!");
            }
        }

        eventTransformer = new SchemaEventTransformer(rules);
    }

    @Override
    public Map<String, Object> process(Map<String, Object> event) {
        return eventTransformer.transform(event);
    }
}
