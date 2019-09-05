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

package org.streampipes.manager.template.instances;

import org.streampipes.commons.exceptions.ElementNotFoundException;
import org.streampipes.manager.template.PipelineTemplateGenerator;
import org.streampipes.model.template.PipelineTemplateDescription;
import org.streampipes.sdk.builder.BoundPipelineElementBuilder;
import org.streampipes.sdk.builder.PipelineTemplateBuilder;

import java.net.URISyntaxException;

public class DelmeExamplePipelineTemplate extends PipelineTemplateGenerator implements PipelineTemplate {

    private static String ID = "org.streampipes.manager.template.instances.DelmeExamplePipelineTemplate";

    @Override
    public PipelineTemplateDescription declareModel() throws URISyntaxException, ElementNotFoundException {
        return new PipelineTemplateDescription(PipelineTemplateBuilder.create("distance-kvi","Distance KVI",
                "Calculates the distance between two locations")
                .setAppId(ID)
                .boundPipelineElementTemplate(BoundPipelineElementBuilder
                        .create(getProcessor("org.streampipes.processors.geo.jvm.google-routing"))
//                    .withPredefinedFreeTextValue("timeWindow", "30")
//                    .withPredefinedSelection("operation", Collections.singletonList("Average"))
//                    .withOverwrittenLabel("aggregate", "Select a field you'd like to use for the KVI calculation")
                        .connectTo(BoundPipelineElementBuilder
//                            .create(getSink("http://localhost:8090/sec/dashboard_sink"))
                                .create(getSink("org.streampipes.sinks.databases.jvm.couchdb"))
                                .withPredefinedFreeTextValue("db_name", "kvi")
                                .build())
                        .build())
                .build());
    }
}
