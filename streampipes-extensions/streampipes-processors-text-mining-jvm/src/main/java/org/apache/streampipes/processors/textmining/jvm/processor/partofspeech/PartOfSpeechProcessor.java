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

package org.apache.streampipes.processors.textmining.jvm.processor.partofspeech;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.ListField;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PartOfSpeechProcessor extends StreamPipesDataProcessor {

  private static final String DETECTION_FIELD_KEY = "detectionField";
  static final String CONFIDENCE_KEY = "confidencePos";
  static final String TAG_KEY = "tagPos";
  private static final String BINARY_FILE_KEY = "binary-file";

  private String detection;
  private POSTaggerME posTagger;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.textmining.jvm.partofspeech")
        .category(DataProcessorType.ENRICH_TEXT)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredFile(Labels.withId(BINARY_FILE_KEY))
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.listRequirement(Datatypes.String),
                Labels.withId(DETECTION_FIELD_KEY),
                PropertyScope.NONE)
            .build())
        .outputStrategy(OutputStrategies.append(
            EpProperties.listDoubleEp(
                Labels.withId(CONFIDENCE_KEY),
                CONFIDENCE_KEY,
                "http://schema.org/ItemList"),
            EpProperties.listStringEp(
                Labels.withId(TAG_KEY),
                TAG_KEY,
                "http://schema.org/ItemList")))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    String filename = parameters.extractor().selectedFilename(BINARY_FILE_KEY);
    byte[] fileContent = runtimeContext.getStreamPipesClient().fileApi().getFileContent(filename);
    this.detection = parameters.extractor().mappingPropertyValue(DETECTION_FIELD_KEY);

    InputStream modelIn = new ByteArrayInputStream(fileContent);
    POSModel model;
    try {
      model = new POSModel(modelIn);
    } catch (IOException e) {
      throw new SpRuntimeException("Error when loading the uploaded model.", e);
    }

    posTagger = new POSTaggerME(model);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    ListField text = event.getFieldBySelector(detection).getAsList();

    String[] tags = posTagger.tag(text.castItems(String.class).toArray(String[]::new));
    double[] confidence = posTagger.probs();


    event.addField(PartOfSpeechProcessor.CONFIDENCE_KEY, confidence);
    event.addField(PartOfSpeechProcessor.TAG_KEY, tags);

    collector.collect(event);
  }

  @Override
  public void onDetach() throws SpRuntimeException {
  }
}
