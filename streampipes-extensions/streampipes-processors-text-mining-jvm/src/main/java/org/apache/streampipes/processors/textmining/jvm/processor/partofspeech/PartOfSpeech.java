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
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.ListField;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PartOfSpeech implements EventProcessor<PartOfSpeechParameters> {

  private static Logger log;

  private String detection;
  private POSTaggerME posTagger;

  public PartOfSpeech() {
  }

  @Override
  public void onInvocation(PartOfSpeechParameters partOfSpeechParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    log = partOfSpeechParameters.getGraph().getLogger(PartOfSpeech.class);
    this.detection = partOfSpeechParameters.getDetectionName();

    InputStream modelIn = new ByteArrayInputStream(partOfSpeechParameters.getFileContent());
    POSModel model = null;
    try {
      model = new POSModel(modelIn);
    } catch (IOException e) {
      throw new SpRuntimeException("Error when loading the uploaded model.", e);
    }

    posTagger = new POSTaggerME(model);
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
