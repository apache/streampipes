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
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import java.util.HashMap;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JSEval implements EventProcessor<JSEvalParameters> {
    private static final String ENGINE_NAME = "JavaScript";
    private ScriptEngine engine;
    private String code;

    @Override
    public void onInvocation(JSEvalParameters parameters, SpOutputCollector spOutputCollector,
                             EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
        ScriptEngineManager factory = new ScriptEngineManager();
        engine = factory.getEngineByName(ENGINE_NAME);
        code = parameters.getCode();
        try {
            engine.eval(code);
        } catch (ScriptException e) {
            throw new SpRuntimeException("Error in script: " + e.getMessage());
        }
    }

    @Override
    public void onEvent(Event event, SpOutputCollector outputCollector) throws SpRuntimeException {
        // create new event with input event's source info and schema info.
        Event outEvent = new Event(new HashMap<>(), event.getSourceInfo(), event.getSchemaInfo());
        try {
            final Map<String, Object> eventData = event.getRaw();
            Object result = ((Invocable) engine).invokeFunction("process", eventData);
            Map<String, Object> output = (Map<String, Object>) result;
            output.forEach(outEvent::addField);
        } catch (ScriptException e) {
            throw new SpRuntimeException("Error in script: " + e.getMessage());
        } catch (ClassCastException e) {
            throw new SpRuntimeException("`process` method must return a map with new event data.");
        } catch (NoSuchMethodException e) {
            throw new SpRuntimeException("`process(event){ return {}; };` method not found in script: " + code);
        }
        outputCollector.collect(outEvent);
    }

    @Override
    public void onDetach() {
        // do nothing.
    }
}
