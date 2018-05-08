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

import org.streampipes.model.SpDataStream;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.vocabulary.MhWirth;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Groundings;
import org.streampipes.sdk.utils.Datatypes;
import org.junit.Test;

public class TestSpDataStreamBuilder {

    @Test
    public void testDataStreamBuilder() {

        SpDataStream stream = new DataStreamBuilder("test", "label", "description")
                .format(Groundings.jsonFormat())
                .protocol(Groundings.kafkaGrounding("ipe-koi15.fzi.de", 9092, "abc"))
                .property(EpProperties.integerEp(Labels.empty(), "randomNumber", MhWirth.DrillingStatus))
                .property(PrimitivePropertyBuilder
                        .create(Datatypes.String, "randomLetter")
                .label("label")
                .description("description")
                .valueSpecification(0.0f, 100.0f, 1.0f).build())
                .build();
    }
}
