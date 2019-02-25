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

package org.streampipes.processors.enricher.flink.processor.urldereferencing;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.enricher.flink.config.EnricherFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.vocabulary.SO;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class UrlDereferencingController extends FlinkDataProcessorDeclarer<UrlDereferencingParameter> {

    private final String APPEND_HTML = "appendHtml";
    private final String URL = "url";

    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("org.streampipes.processors.enricher.flink.processor.urldereferencing",
                "URL Dereferencing","Append the html page as a string to event")
                .iconUrl(EnricherFlinkConfig.getIconUrl("html_icon"))
                .category(DataProcessorType.ENRICH)
                .requiredStream(StreamRequirementsBuilder
                        .create()
                        .requiredPropertyWithUnaryMapping(EpRequirements.stringReq(),
                                Labels.from(URL, "URL", "The server URL"),
                                PropertyScope.NONE)
                        .build())
                .outputStrategy(
                        OutputStrategies.append(
                                EpProperties.numberEp(Labels.empty(), APPEND_HTML, SO.Text)))
                .supportedFormats(SupportedFormats.jsonFormat())
                .supportedProtocols(SupportedProtocols.kafka())
                .build();
    }


    @Override
    public FlinkDataProcessorRuntime<UrlDereferencingParameter> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
        String urlString = extractor.mappingPropertyValue(URL);

//        java.net.URL url = null;
/*        try {
             url = new URL(urlString);
        } catch (MalformedURLException e) {
            logger.error("Malformed URL:" + urlString);
            throw new IllegalArgumentException("Malformed URL:" + urlString);
        }
*/
        UrlDereferencingParameter staticParam = new UrlDereferencingParameter(graph, urlString, APPEND_HTML);

        return  new UrlDereferencingProgram(staticParam, EnricherFlinkConfig.INSTANCE.getDebug());
    }
}
