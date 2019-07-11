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

package org.streampipes.processors.textmining.jvm.processor.chunker;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.util.Span;
import org.streampipes.logging.api.Logger;
import org.streampipes.model.runtime.Event;
import org.streampipes.model.runtime.field.ListField;
import org.streampipes.processors.textmining.jvm.processor.TextMiningUtil;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Chunker implements EventProcessor<ChunkerParameters> {

  private static Logger LOG;

  private String tags;
  private String tokens;
  private ChunkerME chunker;

  public Chunker() {
    try (InputStream modelIn = getClass().getClassLoader().getResourceAsStream("chunker-en.bin")) {
      ChunkerModel model = new ChunkerModel(modelIn);
      chunker = new ChunkerME(model);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onInvocation(ChunkerParameters chunkerParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {
    LOG = chunkerParameters.getGraph().getLogger(Chunker.class);
    this.tags = chunkerParameters.getTags();
    this.tokens = chunkerParameters.getTokens();
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {
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
