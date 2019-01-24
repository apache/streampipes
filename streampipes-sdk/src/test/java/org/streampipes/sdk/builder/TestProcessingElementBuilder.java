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

package org.streampipes.sdk.builder;

import org.junit.Test;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OutputStrategies;

public class TestProcessingElementBuilder {

    @Test
    public void testProcessingElementBuilderGeneration() {

        DataProcessorDescription testDescription = ProcessingElementBuilder
                .create("test-element", "title", "description")
                .iconUrl("url")
                .requiredTextParameter("requiredText", "requiredTextLabel", "requiredTextDescription")
                .requiredIntegerParameter("requiredInteger", "requiredIntegerLabel", "requiredIntegerDescription")
                .requiredFloatParameter("requiredFloat", "requiredFloatLabel", "requiredFloatDescription")
                .requiredStream(StreamRequirementsBuilder.create().requiredProperty
                        (EpRequirements.numberReq()).requiredPropertyWithUnaryMapping
                        (EpRequirements.booleanReq(), Labels.from("internalName", "label",
                                "description"), PropertyScope.NONE).build())
                .outputStrategy(OutputStrategies.custom())
                .build();
    }
}
