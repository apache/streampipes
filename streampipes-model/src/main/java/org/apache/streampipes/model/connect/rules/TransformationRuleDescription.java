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

package org.apache.streampipes.model.connect.rules;

import org.apache.streampipes.model.connect.rules.schema.CreateNestedRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.MoveRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.ChangeDatatypeTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.CorrectionValueTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.apache.streampipes.model.shared.annotation.TsModel;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
@TsModel
@JsonSubTypes({
    @JsonSubTypes.Type(AddTimestampRuleDescription.class),
    @JsonSubTypes.Type(AddValueTransformationRuleDescription.class),
    @JsonSubTypes.Type(TimestampTranfsformationRuleDescription.class),
    @JsonSubTypes.Type(UnitTransformRuleDescription.class),
    @JsonSubTypes.Type(EventRateTransformationRuleDescription.class),
    @JsonSubTypes.Type(RemoveDuplicatesTransformationRuleDescription.class),
    @JsonSubTypes.Type(CreateNestedRuleDescription.class),
    @JsonSubTypes.Type(DeleteRuleDescription.class),
    @JsonSubTypes.Type(RenameRuleDescription.class),
    @JsonSubTypes.Type(MoveRuleDescription.class),
    @JsonSubTypes.Type(ChangeDatatypeTransformationRuleDescription.class),
    @JsonSubTypes.Type(CorrectionValueTransformationRuleDescription.class),
})
public abstract class TransformationRuleDescription {


  public TransformationRuleDescription() {
    super();
  }

  public abstract void accept(ITransformationRuleVisitor visitor);

  public abstract int getRulePriority();
}
