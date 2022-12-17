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

import org.apache.streampipes.manager.selector.PropertySelectorGenerator;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.constants.PropertySelectorConstants;

import java.util.ArrayList;
import java.util.List;

public class EmptyRequirementsSelectorGenerator extends AbstractRequirementsSelectorGenerator {

  public EmptyRequirementsSelectorGenerator(List<SpDataStream> inputStreams) {
    super(inputStreams);
  }

  @Override
  public List<String> generateSelectors() {
    List<String> selectors = new ArrayList<>(new PropertySelectorGenerator(inputStreams
        .get(0).getEventSchema().getEventProperties(), true)
        .generateSelectors(PropertySelectorConstants.FIRST_STREAM_ID_PREFIX));

    if (inputStreams.size() > 1) {
      selectors.addAll(new PropertySelectorGenerator(inputStreams
          .get(1).getEventSchema().getEventProperties(), true)
          .generateSelectors(PropertySelectorConstants.SECOND_STREAM_ID_PREFIX));
    }

    return selectors;
  }
}
