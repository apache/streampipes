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

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class NameFinderParameters extends EventProcessorBindingParams {
    private String tokens;
    private String model;

    public NameFinderParameters(DataProcessorInvocation graph, String tokens, String model)
    {
        super(graph);
        this.tokens = tokens;
        this.model = model;
    }

    public String getTokens() {
        return tokens;
    }
    public String getModel() {
        return model;
    }
}
