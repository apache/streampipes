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

package org.apache.streampipes.sdk.builder;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.vocabulary.MhWirth;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.Groundings;
import org.apache.streampipes.sdk.utils.Datatypes;
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
