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

package org.streampipes.processors.textmining.jvm.processor.partofspeech;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.streampipes.logging.api.Logger;
import org.streampipes.model.runtime.Event;
import org.streampipes.model.runtime.field.ListField;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.io.IOException;
import java.io.InputStream;

public class PartOfSpeech implements EventProcessor<PartOfSpeechParameters> {

  private static Logger LOG;

  private String detection;
  private POSTaggerME posTagger;

  public PartOfSpeech() {
    try (InputStream modelIn = getClass().getClassLoader().getResourceAsStream("partofspeech-en-v2.bin")) {
      POSModel model = new POSModel(modelIn);
      posTagger = new POSTaggerME(model);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onInvocation(PartOfSpeechParameters partOfSpeechParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {
    LOG = partOfSpeechParameters.getGraph().getLogger(PartOfSpeech.class);
    this.detection = partOfSpeechParameters.getDetectionName();
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {
    ListField text = inputEvent.getFieldBySelector(detection).getAsList();

    String[] tags = posTagger.tag(text.castItems(String.class).stream().toArray(String[]::new));
    double[] confidence = posTagger.probs();
    
    inputEvent.addField(PartOfSpeechController.CONFIDENCE_KEY, confidence);
    inputEvent.addField(PartOfSpeechController.TAG_KEY, tags);

    out.collect(inputEvent);
  }

  @Override
  public void onDetach() {
  }
}
