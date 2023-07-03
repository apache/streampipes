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

package org.apache.streampipes.pe.flink.processor.boilerplate;

import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorProgram;

public class BoilerplateController extends FlinkDataProcessorDeclarer<BoilerplateParameters> {

  public static final String HTML_PROPERTY = "stringProperty";
  public static final String EXTRACTOR = "extractor";
  public static final String OUTPUT_MODE = "outputMode";


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create(
            "org.apache.streampipes.processors.transformation.flink.processor.boilerplate")
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.stringReq(),
                Labels.withId(HTML_PROPERTY),
                PropertyScope.NONE)
            .build())
        .requiredSingleValueSelection(Labels.withId(EXTRACTOR),
            Options.from("Article Extractor", "Default Extractor", "Largest Content Extractor", "Canola Extractor",
                "Keep Everything Extractor"))
        .requiredSingleValueSelection(Labels.withId(OUTPUT_MODE),
            Options.from("Plain Text", "Highlighted Html", "Html"))
        .outputStrategy(OutputStrategies.keep())
        .build();
  }

  @Override
  public FlinkDataProcessorProgram<BoilerplateParameters> getProgram(DataProcessorInvocation graph,
                                                                     ProcessingElementParameterExtractor extractor) {
    String htmlProperty = extractor.mappingPropertyValue(HTML_PROPERTY);
    String htmlExtractor = extractor.selectedSingleValue(EXTRACTOR, String.class);
    String htmlOutputMode = extractor.selectedSingleValue(OUTPUT_MODE, String.class);

    ExtractorMode extractorMode = null;
    switch (htmlExtractor) {
      case "Article Extractor":
        extractorMode = ExtractorMode.ARTICLE;
        break;
      case "Default Extractor":
        extractorMode = ExtractorMode.DEFAULT;
        break;
      case "Largest Content Extractor":
        extractorMode = ExtractorMode.LARGEST_CONTENT;
        break;
      case "Canola Extractor":
        extractorMode = ExtractorMode.CANOLA;
        break;
      case "Keep Everything Extractor":
        extractorMode = ExtractorMode.KEEP_EVERYTHING;
    }

    OutputMode outputMode = null;
    switch (htmlOutputMode) {
      case "Plain Text":
        outputMode = OutputMode.PLAIN_TEXT;
        break;
      case "Highlighted Html":
        outputMode = OutputMode.HIGHLIGHTED_HTML;
        break;
      case "Html":
        outputMode = OutputMode.HTML;
    }

    BoilerplateParameters staticParams = new BoilerplateParameters(graph, htmlProperty, extractorMode, outputMode);

    return new BoilerplateProgram(staticParams);
  }
}
