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

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.staticproperty.MappingProperty;

import java.util.List;

public class RequirementsSelectorGeneratorFactory {

  public static AbstractRequirementsSelectorGenerator getRequirementsSelector(
      MappingProperty mappingProperty,
      List<SpDataStream> inputStreams,
      InvocableStreamPipesEntity rootPipelineElement) {
    if (!mappingProperty.getRequirementSelector().equals("")) {
      return new RequirementsSelectorGenerator(inputStreams,
          rootPipelineElement,
          mappingProperty.getRequirementSelector());
    } else {
      return new EmptyRequirementsSelectorGenerator(inputStreams);
    }
  }
}
