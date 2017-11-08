package org.streampipes.sdk.builder;

import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.vocabulary.MhWirth;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.junit.Test;

/**
 * Created by riemer on 04.12.2016.
 */
public class TestProcessingElementBuilder {

    @Test
    public void testProcessingElementBuilderGeneration() {

        SepaDescription testDescription = ProcessingElementBuilder
                .create("test-element", "title", "description")
                .iconUrl("url")
                .requiredTextParameter("requiredText", "requiredTextLabel", "requiredTextDescription")
                .requiredIntegerParameter("requiredInteger", "requiredIntegerLabel", "requiredIntegerDescription")
                .requiredFloatParameter("requiredFloat", "requiredFloatLabel", "requiredFloatDescription")
                .requiredPropertyStream1(EpRequirements.numberReq())
                .requiredPropertyStream1WithUnaryMapping(EpRequirements.booleanReq(), "internalName", "label", "description")
                .requiredPropertyStream1(EpRequirements.domainPropertyReq(MhWirth.DrillingStatus))
                .requiredPropertyStream2(EpRequirements.booleanReq())
                .outputStrategy(OutputStrategies.custom())
                .build();
    }
}
