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
package org.streampipes.manager.selector;

import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.DataStreamBuilder;
import org.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.ValueSpecifications;
import org.streampipes.sdk.utils.Datatypes;
import org.streampipes.vocabulary.Geo;
import org.streampipes.vocabulary.SO;

public class TestSelectorUtils {

  public static EventSchema makeSchema() {
    return DataStreamBuilder.create("complex-stream", "", "")
            .property(EpProperties.timestampProperty("timestamp"))
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.Integer, "testMeasurement")
                    .domainProperty(SO.Number)
                    .scope(PropertyScope.MEASUREMENT_PROPERTY)
                    .build())
            .property(PrimitivePropertyBuilder
                    .create(Datatypes.Integer, "testDimension")
                    .domainProperty(SO.Number)
                    .scope(PropertyScope.DIMENSION_PROPERTY)
                    .build())
            .property(EpProperties.stringEp(Labels.withTitle("string", "string description"), "testString",
                    "http://test.de", ValueSpecifications.from("A", "B", "C")))
            .property(EpProperties.stringEp(Labels.withTitle("string2", "string description"), "testString2",
                    "http://test.de", ValueSpecifications.from("A", "B", "C", "D")))
            .property(EpProperties.integerEp(Labels.withTitle("integer2", "integerDescription"), "testInteger2",
                    SO.Number, ValueSpecifications.from(0.0f, 1.0f, 1.f)))
            .property(EpProperties.integerEp(Labels.withTitle("integer", "integerDescription"), "testInteger",
                    SO.Number, ValueSpecifications.from(10.0f, 100.0f, 10.0f)))
            .property(EpProperties.nestedEp(Labels.from("location", "", ""), "location",
                    EpProperties.doubleEp(Labels.withId("latitude"), "latitude", Geo
                            .lat),
                    EpProperties.doubleEp(Labels.withId("longitude"), "longitude", Geo.lng)))
            .build()
            .getEventSchema();
  }

  public static EventSchema makeSimpleSchema() {
    return DataStreamBuilder.create("simple-stream", "", "")
            .property(EpProperties.timestampProperty("timestamp"))
            .build()
            .getEventSchema();
  }
}
