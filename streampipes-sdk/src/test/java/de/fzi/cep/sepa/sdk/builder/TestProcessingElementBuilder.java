package de.fzi.cep.sepa.sdk.builder;

import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.vocabulary.MhWirth;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.sdk.helpers.OutputStrategies;
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
                .stream1PropertyRequirementWithUnaryMapping(EpRequirements.booleanReq(), "internalName", "label", "description")
                .requiredPropertyStream1(EpRequirements.domainPropertyReq(MhWirth.DrillingStatus))
                .requiredPropertyStream2(EpRequirements.booleanReq())
                .outputStrategy(OutputStrategies.custom())
                .build();
    }
}
