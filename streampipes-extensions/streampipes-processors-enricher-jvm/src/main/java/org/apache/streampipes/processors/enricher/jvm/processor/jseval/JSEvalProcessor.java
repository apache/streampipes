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
package org.apache.streampipes.processors.enricher.jvm.processor.jseval;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.CodeLanguage;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

import java.util.HashMap;
import java.util.Map;

public class JSEvalProcessor extends StreamPipesDataProcessor {

  private static final String JS_FUNCTION = "jsFunction";

  private Context polyglot;
  private Value function;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.enricher.jvm.jseval")
        .category(DataProcessorType.SCRIPTING)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .requiredCodeblock(Labels.withId(JS_FUNCTION), CodeLanguage.Javascript)
        .outputStrategy(OutputStrategies.userDefined())
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    polyglot = Context.create();
    String code = parameters.extractor().codeblockValue(JS_FUNCTION);
    function = polyglot.eval("js", "(" + code + ")");
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    // create new event with input event's source info and schema info.
    Event outEvent = new Event(new HashMap<>(), event.getSourceInfo(), event.getSchemaInfo());
    try {

      final Map<String, Object> eventData = event.getRaw();

      Object result = function.execute(ProxyObject.fromMap(eventData));
      Map<String, Object> resultEvent = ((Value) result).as(java.util.Map.class);
      if (resultEvent != null) {
        resultEvent.forEach(outEvent::addField);
        collector.collect(outEvent);
      }

    } catch (ClassCastException e) {
      throw new SpRuntimeException("`process` method must return a map with new event data.");
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
