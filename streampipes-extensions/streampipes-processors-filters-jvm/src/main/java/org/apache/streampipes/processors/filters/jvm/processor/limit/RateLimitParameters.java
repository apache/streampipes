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

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.processors.filters.jvm.processor.limit.util.EventSelection;
import org.apache.streampipes.processors.filters.jvm.processor.limit.util.WindowType;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class RateLimitParameters extends EventProcessorBindingParams {
    private EventSelection eventSelection;
    private WindowType windowType;
    private Boolean groupingEnabled;
    private String groupingField;
    private Object windowExpression;

    public RateLimitParameters(DataProcessorInvocation graph,
                               WindowType windowType,
                               Object windowExpression,
                               Boolean groupingEnabled,
                               String groupingField,
                               EventSelection eventSelection) {
        super(graph);
        this.eventSelection = eventSelection;
        this.windowType = windowType;
        this.groupingEnabled = groupingEnabled;
        this.groupingField = groupingField;
        this.windowExpression = windowExpression;
    }

    public WindowType getWindowType() {
        return windowType;
    }

    public Object getWindowExpression() {
        return windowExpression;
    }

    public Boolean getGroupingEnabled() {
        return groupingEnabled;
    }

    public String getGroupingField() {
        return groupingField;
    }

    public EventSelection getEventSelection() {
        return eventSelection;
    }

}
