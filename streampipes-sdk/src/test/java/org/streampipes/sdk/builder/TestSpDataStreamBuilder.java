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
