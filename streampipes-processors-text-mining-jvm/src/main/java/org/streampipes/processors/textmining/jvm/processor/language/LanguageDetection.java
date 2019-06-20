/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.processors.textmining.jvm.processor.language;

import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import org.streampipes.logging.api.Logger;
import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LanguageDetection implements EventProcessor<LanguageDetectionParameters> {

  private static Logger LOG;

  private String detection;
  private LanguageDetector myCategorizer;

  public LanguageDetection() {
    try (InputStream modelIn = new FileInputStream("language-detection.bin")) {
      LanguageDetectorModel model = new LanguageDetectorModel(modelIn);
      myCategorizer = new LanguageDetectorME(model);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onInvocation(LanguageDetectionParameters languageDetectionParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {
    LOG = languageDetectionParameters.getGraph().getLogger(LanguageDetection.class);
    this.detection = languageDetectionParameters.getDetectionName();
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {
    String text = inputEvent.getFieldBySelector(detection).getAsPrimitive().getAsString();
    Language language = myCategorizer.predictLanguage(text);

    inputEvent.addField(LanguageDetectionController.LANGUAGE_KEY, language.getLang());
    inputEvent.addField(LanguageDetectionController.CONFIDENCE_KEY, language.getConfidence());

    out.collect(inputEvent);
  }

  @Override
  public void onDetach() {
  }
}
