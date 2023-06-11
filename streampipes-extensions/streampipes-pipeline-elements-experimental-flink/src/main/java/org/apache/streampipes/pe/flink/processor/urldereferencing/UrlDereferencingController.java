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

package org.apache.streampipes.pe.flink.processor.urldereferencing;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorProgram;

public class UrlDereferencingController extends FlinkDataProcessorDeclarer<UrlDereferencingParameter> {

  private static final String APPEND_HTML = "appendHtml";
  private static final String URL = "url";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(
            "org.apache.streampipes.processors.enricher.flink.processor.urldereferencing")
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .category(DataProcessorType.ENRICH)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.stringReq(),
                Labels.withId(URL),
                PropertyScope.NONE)
            .build())
        .outputStrategy(
            OutputStrategies.append(
                EpProperties.stringEp(Labels.empty(), APPEND_HTML, SO.TEXT)))
        .build();
  }


  @Override
  public FlinkDataProcessorProgram<UrlDereferencingParameter> getProgram(DataProcessorInvocation graph,
                                                                         ProcessingElementParameterExtractor ex) {
    String urlString = ex.mappingPropertyValue(URL);
    UrlDereferencingParameter staticParam = new UrlDereferencingParameter(graph, urlString, APPEND_HTML);

    return new UrlDereferencingProgram(staticParam);
  }
}
