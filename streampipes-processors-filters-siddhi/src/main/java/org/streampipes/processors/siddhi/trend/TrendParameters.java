/*
 * Copyright 2019 FZI Forschungszentrum Informatik
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

package org.streampipes.processors.siddhi.trend;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class TrendParameters extends EventProcessorBindingParams {

    private TrendOperator operation;
    private int increase;
    private int duration;

    private String mapping;


    public TrendParameters(DataProcessorInvocation invocationGraph,
                              TrendOperator operation, int increase, int duration,
                              String mapping) {
        super(invocationGraph);
        this.operation = operation;
        this.increase = increase;
        this.duration = duration;
        this.mapping = mapping;
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

}
