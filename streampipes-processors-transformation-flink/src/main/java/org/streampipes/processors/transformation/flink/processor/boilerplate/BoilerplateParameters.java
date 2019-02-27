/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.processors.transformation.flink.processor.boilerplate;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class BoilerplateParameters extends EventProcessorBindingParams {

    private String htmlProperty;
    private ExtractorMode extractorMode;
    private OutputMode outputMode;

    public BoilerplateParameters(DataProcessorInvocation graph, String htmlProperty, ExtractorMode extractorMode, OutputMode outputMode) {
        super(graph);
        this.htmlProperty = htmlProperty;
        this.extractorMode = extractorMode;
        this.outputMode = outputMode;
    }

    public String getHtmlProperty() {
        return htmlProperty;
    }

    public ExtractorMode getExtractorMode() {
        return extractorMode;
    }

    public OutputMode getOutputMode() {
        return outputMode;
    }
}
