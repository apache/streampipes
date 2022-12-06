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

package org.apache.streampipes.processors.textmining.jvm.processor.chunker;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.ListField;
import org.apache.streampipes.processors.textmining.jvm.processor.TextMiningUtil;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.util.Span;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Chunker implements EventProcessor<ChunkerParameters> {

  private static Logger log;

  private String tags;
  private String tokens;
  private ChunkerME chunker;

  public Chunker() {
//    try (InputStream modelIn = getClass().getClassLoader().getResourceAsStream("chunker-en.bin")) {
//      ChunkerModel model = new ChunkerModel(modelIn);
//      chunker = new ChunkerME(model);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
  }

  @Override
  public void onInvocation(ChunkerParameters chunkerParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    log = chunkerParameters.getGraph().getLogger(Chunker.class);
    this.tags = chunkerParameters.getTags();
    this.tokens = chunkerParameters.getTokens();

    InputStream modelIn = new ByteArrayInputStream(chunkerParameters.getFileContent());
    ChunkerModel model = null;
    try {
      model = new ChunkerModel(modelIn);
    } catch (IOException e) {
      throw new SpRuntimeException("Error when loading the uploaded model.", e);
    }

    chunker = new ChunkerME(model);
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) throws SpRuntimeException {
    ListField tags = inputEvent.getFieldBySelector(this.tags).getAsList();
    ListField tokens = inputEvent.getFieldBySelector(this.tokens).getAsList();


    String[] tagsArray = tags.castItems(String.class).stream().toArray(String[]::new);
    String[] tokensArray = tokens.castItems(String.class).stream().toArray(String[]::new);

    Span[] spans = chunker.chunkAsSpans(tokensArray, tagsArray);

    List<String> chunks = TextMiningUtil.extractSpans(spans, tokensArray);
    String[] types = Arrays.stream(spans).map(s -> s.getType()).toArray(String[]::new);

    inputEvent.addField(ChunkerController.CHUNK_TYPE_FIELD_KEY, types);
    inputEvent.addField(ChunkerController.CHUNK_FIELD_KEY, chunks);

    out.collect(inputEvent);
  }

  @Override
  public void onDetach() {
  }
}
