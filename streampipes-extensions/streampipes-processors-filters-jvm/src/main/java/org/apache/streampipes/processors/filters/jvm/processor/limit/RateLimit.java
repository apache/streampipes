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
package org.apache.streampipes.processors.filters.jvm.processor.limit;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.filters.jvm.processor.limit.util.WindowFactory;
import org.apache.streampipes.processors.filters.jvm.processor.limit.window.Window;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RateLimit implements EventProcessor<RateLimitParameters> {
    private final static String DEFAULT_GROUP = "default";
    private Boolean groupingEnabled;
    private String groupingField;
    private ConcurrentMap<Object, Window> windows;
    private WindowFactory factory;

    @Override
    public void onInvocation(RateLimitParameters parameters,
                             SpOutputCollector outputCollector,
                             EventProcessorRuntimeContext runtimeContext) {
        this.groupingEnabled = parameters.getGroupingEnabled();
        this.groupingField = parameters.getGroupingField();
        this.windows = new ConcurrentHashMap<>();
        this.factory = new WindowFactory(
                parameters.getWindowType(),
                parameters.getWindowExpression(),
                parameters.getEventSelection(),
                outputCollector);
    }

    @Override
    public void onEvent(Event event, SpOutputCollector spOutputCollector) throws SpRuntimeException {
        Object group = groupingEnabled ? getGroupKey(event) : DEFAULT_GROUP;
        Window window = windows.get(group);
        if (window == null) {
            window = factory.create();
            window.init();
            windows.put(group, window);
        }
        window.onEvent(event);
    }

    @Override
    public void onDetach() throws SpRuntimeException {
        for (Window window : this.windows.values()) {
            window.destroy();
        }
    }

    private Object getGroupKey(Event event) {
        return event.getFieldBySelector(groupingField).getAsPrimitive().getAsString();
    }

}
