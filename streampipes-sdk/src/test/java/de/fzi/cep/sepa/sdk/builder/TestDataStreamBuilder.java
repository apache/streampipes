package de.fzi.cep.sepa.sdk.builder;

import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.vocabulary.MhWirth;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.Groundings;
import de.fzi.cep.sepa.sdk.utils.Datatypes;
import org.junit.Test;

/**
 * Created by riemer on 06.12.2016.
 */
public class TestDataStreamBuilder {

    @Test
    public void testDataStreamBuilder() {

        EventStream stream = new DataStreamBuilder("test", "label", "description")
                .format(Groundings.jsonFormat())
                .protocol(Groundings.kafkaGrounding("ipe-koi15.fzi.de", 9092, "abc"))
                .property(EpProperties.integerEp("randomNumber", MhWirth.DrillingStatus))
                .property(PrimitivePropertyBuilder
                        .create(Datatypes.String, "randomLetter")
                .label("label")
                .description("description")
                .valueSpecification(0.0f, 100.0f, 1.0f).build())
                .build();
    }
}
