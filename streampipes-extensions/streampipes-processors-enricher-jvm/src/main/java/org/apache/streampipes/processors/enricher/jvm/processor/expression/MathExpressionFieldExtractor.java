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

package org.apache.streampipes.processors.enricher.jvm.processor.expression;

import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.staticproperty.CodeInputStaticProperty;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.vocabulary.SO;

import java.util.List;

import static org.apache.streampipes.processors.enricher.jvm.processor.expression.MathExpressionProcessor.ENRICHED_FIELDS;
import static org.apache.streampipes.processors.enricher.jvm.processor.expression.MathExpressionProcessor.EXPRESSION;
import static org.apache.streampipes.processors.enricher.jvm.processor.expression.MathExpressionProcessor.FIELD_NAME;

public class MathExpressionFieldExtractor {

  private final DataProcessorInvocation processingElement;
  private final IDataProcessorParameterExtractor extractor;

  public MathExpressionFieldExtractor(DataProcessorInvocation processingElement) {
    this.processingElement = processingElement;
    this.extractor = ProcessingElementParameterExtractor.from(processingElement);
  }

  public MathExpressionFieldExtractor(DataProcessorInvocation processingElement,
                                      IDataProcessorParameterExtractor extractor) {
    this.processingElement = processingElement;
    this.extractor = extractor;
  }

  public List<JexlDescription> getAdditionalFields() {
    return extractor
        .collectionMembersAsGroup(ENRICHED_FIELDS)
        .stream().map(group -> {
          var runtimeName = extractor.extractGroupMember(FIELD_NAME, group).as(FreeTextStaticProperty.class).getValue();
          var expression = extractor.extractGroupMember(EXPRESSION, group).as(CodeInputStaticProperty.class).getValue();
          return new JexlDescription(EpProperties.doubleEp(Labels.empty(), runtimeName, SO.NUMBER), expression);
        }).toList();
  }

  public List<EventProperty> getInputProperties() {
    var inputStreams = processingElement.getInputStreams();
    if (!inputStreams.isEmpty()) {
      return inputStreams.get(0).getEventSchema().getEventProperties();
    } else {
      return List.of();
    }
  }
}

