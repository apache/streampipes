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

package org.streampipes.processors.textmining.jvm.processor.namefinder;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;
import org.streampipes.logging.api.Logger;
import org.streampipes.model.runtime.Event;
import org.streampipes.model.runtime.field.ListField;
import org.streampipes.processors.textmining.jvm.config.TextMiningJvmConfig;
import org.streampipes.processors.textmining.jvm.processor.TextMiningUtil;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class NameFinder implements EventProcessor<NameFinderParameters> {

  private static Logger LOG;

  private String tokens;
  private NameFinderME nameFinder;

  public NameFinder() {

  }

  @Override
  public void onInvocation(NameFinderParameters nameFinderParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {
    LOG = nameFinderParameters.getGraph().getLogger(NameFinder.class);

    String modelPath = TextMiningJvmConfig.INSTANCE.getModelDirectory() + nameFinderParameters.getModel();
    loadModel(modelPath);

    this.tokens = nameFinderParameters.getTokens();
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {
    ListField tokens = inputEvent.getFieldBySelector(this.tokens).getAsList();

    String[] tokensArray = tokens.castItems(String.class).stream().toArray(String[]::new);
    Span[] spans = nameFinder.find(tokensArray);

    // Generating the list of names from the found spans by the nameFinder
    List<String> names = TextMiningUtil.extractSpans(spans, tokensArray);

    nameFinder.clearAdaptiveData();

    inputEvent.addField(NameFinderController.FOUND_NAME_FIELD_KEY, names);
    out.collect(inputEvent);
  }

  @Override
  public void onDetach() {
  }

  private void loadModel(String modelFile) {
    try (InputStream modelIn = new FileInputStream(modelFile)) {
      TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
      nameFinder = new NameFinderME(model);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
