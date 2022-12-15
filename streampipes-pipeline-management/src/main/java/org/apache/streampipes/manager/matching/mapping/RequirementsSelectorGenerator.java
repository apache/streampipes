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
package org.apache.streampipes.manager.matching.mapping;

import org.apache.streampipes.manager.selector.PropertyRequirementSelector;
import org.apache.streampipes.manager.selector.PropertySelectorGenerator;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.schema.EventProperty;

import java.util.List;

public class RequirementsSelectorGenerator extends AbstractRequirementsSelectorGenerator {

  private String requirementSelector;
  private InvocableStreamPipesEntity rootPipelineElement;

  public RequirementsSelectorGenerator(List<SpDataStream> inputStreams, InvocableStreamPipesEntity rootPipelineElement,
                                       String requirementSelector) {
    super(inputStreams);
    this.rootPipelineElement = rootPipelineElement;
    this.requirementSelector = requirementSelector;
  }

  @Override
  public List<String> generateSelectors() {
    PropertyRequirementSelector selector = new PropertyRequirementSelector(requirementSelector);

    EventProperty propertyRequirement = selector.findPropertyRequirement(rootPipelineElement.getStreamRequirements());
    SpDataStream inputStream = selector.getAffectedStream(inputStreams);

    List<String> availablePropertySelectors =
        new PropertySelectorGenerator(inputStream.getEventSchema(), true).generateSelectors(
            selector.getAffectedStreamPrefix());

    return new MappingPropertyCalculator(inputStream.getEventSchema(), availablePropertySelectors,
        propertyRequirement).matchedPropertySelectors();
  }

}
