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

package org.apache.streampipes.connect.shared.preprocessing.elements;

import org.apache.streampipes.connect.shared.preprocessing.Util;
import org.apache.streampipes.connect.shared.preprocessing.transform.TransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.schema.CreateNestedTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.schema.DeleteTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.schema.MoveTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.schema.RenameTransformationRule;
import org.apache.streampipes.connect.shared.preprocessing.transform.schema.SchemaEventTransformer;
import org.apache.streampipes.extensions.api.connect.IAdapterPipelineElement;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.CreateNestedRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransformSchemaAdapterPipelineElement implements IAdapterPipelineElement {

  private SchemaEventTransformer eventTransformer;
  Logger logger = LoggerFactory.getLogger(TransformSchemaAdapterPipelineElement.class);

  public TransformSchemaAdapterPipelineElement(
      List<? extends TransformationRuleDescription> transformationRuleDescriptions) {
    List<TransformationRule> rules = new ArrayList<>();

    // transforms description to actual rules
    for (TransformationRuleDescription ruleDescription : transformationRuleDescriptions) {
      if (ruleDescription instanceof RenameRuleDescription) {
        RenameRuleDescription tmp = (RenameRuleDescription) ruleDescription;
        rules.add(new RenameTransformationRule(Util.toKeyArray(tmp.getOldRuntimeKey()),
            Util.getLastKey(tmp.getNewRuntimeKey())));
      } else if (ruleDescription instanceof MoveRuleDescription) {
        MoveRuleDescription tmp = (MoveRuleDescription) ruleDescription;
        rules.add(new MoveTransformationRule(Util.toKeyArray(tmp.getOldRuntimeKey()),
            Util.toKeyArray(tmp.getNewRuntimeKey())));
      } else if (ruleDescription instanceof CreateNestedRuleDescription) {
        CreateNestedRuleDescription tmp = (CreateNestedRuleDescription) ruleDescription;
        rules.add(new CreateNestedTransformationRule(Util.toKeyArray(tmp.getRuntimeKey())));
      } else if (ruleDescription instanceof DeleteRuleDescription) {
        DeleteRuleDescription tmp = (DeleteRuleDescription) ruleDescription;
        rules.add(new DeleteTransformationRule(Util.toKeyArray(tmp.getRuntimeKey())));
      } else {
        logger.error(
            "Could not find the class for the rule description. This should never happen. "
                + "Talk to admins to extend the rule implementations to get rid of this error!");
      }
    }

    eventTransformer = new SchemaEventTransformer(rules);
  }

  @Override
  public Map<String, Object> process(Map<String, Object> event) {
    return eventTransformer.transform(event);
  }
}
