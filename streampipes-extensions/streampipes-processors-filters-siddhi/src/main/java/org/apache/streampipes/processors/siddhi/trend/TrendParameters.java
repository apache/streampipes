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

package org.apache.streampipes.processors.siddhi.trend;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.List;

public class TrendParameters extends EventProcessorBindingParams {

    private TrendOperator operation;
    private int increase;
    private int duration;

    private String mapping;
    private List<String> outputFieldSelectors;


    public TrendParameters(DataProcessorInvocation invocationGraph,
                              TrendOperator operation, int increase, int duration,
                              String mapping, List<String> outputFieldSelectors) {
        super(invocationGraph);
        this.operation = operation;
        this.increase = increase;
        this.duration = duration;
        this.mapping = mapping;
        this.outputFieldSelectors = outputFieldSelectors;
    }

    public TrendOperator getOperation() {
        return operation;
    }

    public int getIncrease() {
        return increase;
    }

    public int getDuration() {
        return duration;
    }

    public String getMapping() {
        return mapping;
    }

    public List<String> getOutputFieldSelectors() {
        return outputFieldSelectors;
    }
}
