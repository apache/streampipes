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

package org.streampipes.connect.adapter.generic.pipeline;

import java.util.List;
import java.util.Map;

public class AdapterPipeline {

    private List<AdapterPipelineElement> pipelineElements;


    public AdapterPipeline(List<AdapterPipelineElement> pipelineElements) {
        this.pipelineElements = pipelineElements;
    }

    public void process(Map<String, Object> event) {

        for (AdapterPipelineElement pipelineElement : pipelineElements) {
            event = pipelineElement.process(event);
        }

    }

    public List<AdapterPipelineElement> getPipelineElements() {
        return pipelineElements;
    }

    public void setPipelineElements(List<AdapterPipelineElement> pipelineElements) {
        this.pipelineElements = pipelineElements;
    }
}
