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

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.transformation.flink.config.TransformationFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class BoilerplateController extends FlinkDataProcessorDeclarer<BoilerplateParameters> {

    public static final String HTML_PROPERTY = "stringProperty";
    public static final String EXTRACTOR = "extractor";
    public static final String OUTPUT_MODE = "outputMode";


    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("org.streampipes.processors.transformation.flink.processor.boilerplate",
                "Boilerplate Removal", "Removes boilerplate and extract fulltext from HTML")
                .iconUrl(TransformationFlinkConfig.getIconUrl("Boilerplate_icon"))
                .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(EpRequirements.stringReq(),
                            Labels.from(HTML_PROPERTY,"Html", "The property with the html"), PropertyScope.NONE)
                    .build())
                .requiredSingleValueSelection(Labels.from(EXTRACTOR, "Extractor", "Common use: Article Extractor"),
                        Options.from("Article Extractor", "Default Extractor", "Largest Content Extractor", "Canola Extractor", "Keep Everything Extractor"))
                .requiredSingleValueSelection(Labels.from(OUTPUT_MODE, "Output Mode", ""),
                        Options.from("Plain Text", "Highlighted Html", "Html"))
                .supportedProtocols(SupportedProtocols.kafka())
                .supportedFormats(SupportedFormats.jsonFormat())
                .outputStrategy(OutputStrategies.keep())
                .build();
    }

    @Override
    public FlinkDataProcessorRuntime<BoilerplateParameters> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
        String htmlProperty = extractor.mappingPropertyValue(HTML_PROPERTY);
        String htmlExtractor = extractor.selectedSingleValue(EXTRACTOR, String.class);
        String htmlOutputMode = extractor.selectedSingleValue(OUTPUT_MODE, String.class);

        ExtractorMode extractorMode = null;
        switch (htmlExtractor) {
            case "Article Extractor": extractorMode = ExtractorMode.ARTICLE;
                break;
            case "Default Extractor": extractorMode = ExtractorMode.DEFAULT;
                break;
            case "Largest Content Extractor": extractorMode = ExtractorMode.LARGEST_CONTENT;
                break;
            case "Canola Extractor": extractorMode = ExtractorMode.CANOLA;
                break;
            case "Keep Everything Extractor": extractorMode = ExtractorMode.KEEP_EVERYTHING;
        }

        OutputMode outputMode = null;
        switch (htmlOutputMode) {
            case "Plain Text": outputMode = OutputMode.PLAIN_TEXT;
                break;
            case "Highlighted Html": outputMode = OutputMode.HIGHLIGHTED_HTML;
                break;
            case "Html": outputMode = OutputMode.HTML;
        }

        BoilerplateParameters staticParams = new BoilerplateParameters(graph, htmlProperty, extractorMode, outputMode);

        return new BoilerplateProgram(staticParams, TransformationFlinkConfig.INSTANCE.getDebug());
    }
}
