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

package org.apache.streampipes.processors.textmining.jvm.processor.language;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.processor.DataProcessorConfiguration;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;

import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LanguageDetectionProcessor implements IStreamPipesDataProcessor {

  private static final String DETECTION_FIELD_KEY = "detectionField";
  static final String LANGUAGE_KEY = "language";
  static final String CONFIDENCE_KEY = "confidenceLanguage";
  private static final String BINARY_FILE_KEY = "binary-file";

  private String detection;
  private LanguageDetector languageDetector;

  @Override
  public IDataProcessorConfiguration declareConfig() {
    return DataProcessorConfiguration.create(
        LanguageDetectionProcessor::new,
        ProcessingElementBuilder
            .create("org.apache.streampipes.processors.textmining.jvm.languagedetection", 0)
            .category(DataProcessorType.ENRICH_TEXT)
            .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .withLocales(Locales.EN)
            .requiredFile(Labels.withId(BINARY_FILE_KEY))
            .requiredStream(StreamRequirementsBuilder
                                .create()
                                .requiredPropertyWithUnaryMapping(
                                    EpRequirements.stringReq(),
                                    Labels.withId(DETECTION_FIELD_KEY),
                                    PropertyScope.NONE
                                )
                                .build())
            .outputStrategy(OutputStrategies.append(
                EpProperties.stringEp(
                    Labels.withId(LANGUAGE_KEY),
                    LANGUAGE_KEY,
                    "http://schema.org/language"
                ),
                EpProperties.doubleEp(
                    Labels.withId(CONFIDENCE_KEY),
                    CONFIDENCE_KEY,
                    "https://schema.org/Float"
                )
            ))
            .build()
    );
  }

  @Override
  public void onPipelineStarted(
      IDataProcessorParameters params,
      SpOutputCollector collector,
      EventProcessorRuntimeContext context
  ) {
    String filename = params.extractor()
                            .selectedFilename(BINARY_FILE_KEY);
    byte[] fileContent = context.getStreamPipesClient()
                                .fileApi()
                                .getFileContent(filename);
    this.detection = params.extractor()
                           .mappingPropertyValue(DETECTION_FIELD_KEY);

    InputStream modelIn = new ByteArrayInputStream(fileContent);
    LanguageDetectorModel model;
    try {
      model = new LanguageDetectorModel(modelIn);
    } catch (IOException e) {
      throw new SpRuntimeException("Error when loading the uploaded model.", e);
    }

    languageDetector = new LanguageDetectorME(model);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    String text = event.getFieldBySelector(detection)
                       .getAsPrimitive()
                       .getAsString();
    Language language = languageDetector.predictLanguage(text);

    event.addField(LanguageDetectionProcessor.LANGUAGE_KEY, language.getLang());
    event.addField(LanguageDetectionProcessor.CONFIDENCE_KEY, language.getConfidence());

    collector.collect(event);
  }

  @Override
  public void onPipelineStopped() {
  }
}
