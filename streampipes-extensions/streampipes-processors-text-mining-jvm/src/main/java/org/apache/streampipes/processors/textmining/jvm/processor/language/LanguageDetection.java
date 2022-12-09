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
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LanguageDetection implements EventProcessor<LanguageDetectionParameters> {

  private static Logger log;

  private String detection;
  private LanguageDetector languageDetector;

  public LanguageDetection() {
  }

  @Override
  public void onInvocation(LanguageDetectionParameters languageDetectionParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    log = languageDetectionParameters.getGraph().getLogger(LanguageDetection.class);
    this.detection = languageDetectionParameters.getDetectionName();

    InputStream modelIn = new ByteArrayInputStream(languageDetectionParameters.getFileContent());
    LanguageDetectorModel model = null;
    try {
      model = new LanguageDetectorModel(modelIn);
    } catch (IOException e) {
      throw new SpRuntimeException("Error when loading the uploaded model.", e);
    }

    languageDetector = new LanguageDetectorME(model);
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {
    String text = inputEvent.getFieldBySelector(detection).getAsPrimitive().getAsString();
    Language language = languageDetector.predictLanguage(text);

    inputEvent.addField(LanguageDetectionController.LANGUAGE_KEY, language.getLang());
    inputEvent.addField(LanguageDetectionController.CONFIDENCE_KEY, language.getConfidence());

    out.collect(inputEvent);
  }

  @Override
  public void onDetach() {
  }
}
