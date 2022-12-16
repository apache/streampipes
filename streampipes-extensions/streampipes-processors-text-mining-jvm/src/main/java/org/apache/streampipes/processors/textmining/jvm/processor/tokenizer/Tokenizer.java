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

package org.apache.streampipes.processors.textmining.jvm.processor.tokenizer;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Tokenizer implements EventProcessor<TokenizerParameters> {

  private static Logger log;

  // Field with the text
  private String detection;
  private TokenizerME tokenizer;

  public Tokenizer() {
  }

  @Override
  public void onInvocation(TokenizerParameters tokenizerParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    log = tokenizerParameters.getGraph().getLogger(Tokenizer.class);
    this.detection = tokenizerParameters.getDetectionName();

    InputStream modelIn = new ByteArrayInputStream(tokenizerParameters.getFileContent());
    TokenizerModel model = null;
    try {
      model = new TokenizerModel(modelIn);
    } catch (IOException e) {
      throw new SpRuntimeException("Error when loading the uploaded model.", e);
    }

    tokenizer = new TokenizerME(model);
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {
    String text = inputEvent.getFieldBySelector(detection).getAsPrimitive().getAsString();

    inputEvent.addField(TokenizerController.TOKEN_LIST_FIELD_KEY, tokenizer.tokenize(text));

    out.collect(inputEvent);
  }

  @Override
  public void onDetach() {
  }
}
