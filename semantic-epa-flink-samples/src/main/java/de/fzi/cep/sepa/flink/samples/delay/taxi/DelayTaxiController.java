package de.fzi.cep.sepa.flink.samples.delay.taxi;

import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.sdk.helpers.OutputStrategies;
import de.fzi.cep.sepa.sdk.helpers.SupportedFormats;
import de.fzi.cep.sepa.sdk.helpers.SupportedProtocols;

public class DelayTaxiController extends AbstractFlinkAgentDeclarer<DelayTaxiParameters> {

    public static String OUTPUT_LABEL = "delay_label";
    private static String DELAY_VALUE_NAME = "delay_value";
    private static String LABEL_PROPERTY_NAME = "label_property";


    @Override
    public SepaDescription declareModel() {
        SepaDescription delayDescription = ProcessingElementBuilder
                .create("delay_taxi", "Delay Taxi", "Waits a configured time and adds the labeld to the correspondig grid cell to " +
                        "the event")
                .iconUrl("url")
                .supportedProtocols(SupportedProtocols.kafka())
                .supportedFormats(SupportedFormats.jsonFormat())
                .requiredIntegerParameter(DELAY_VALUE_NAME, "Delay Value [min]", "Minutes till the correct label is knonwn")
                .stream1PropertyRequirementWithUnaryMapping(EpRequirements.numberReq(), LABEL_PROPERTY_NAME,
                        "Label Property", "The property that is selected for the label")
                //TODO add cell id requirement
//                .requiredPropertyStream1(EpRequirements.stringReq("cellid"))
                .outputStrategy(OutputStrategies.append(new EventPropertyPrimitive(
                        XSD._long.toString(), OUTPUT_LABEL, "",
                        de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"))
                ))
                .build();

        return delayDescription;
    }

    @Override
    protected FlinkSepaRuntime<DelayTaxiParameters> getRuntime(SepaInvocation graph) {

        int delayValue = Integer.parseInt(SepaUtils.getFreeTextStaticPropertyValue(graph, DELAY_VALUE_NAME));
        String labelPropertyMapping = SepaUtils.getMappingPropertyName(graph, LABEL_PROPERTY_NAME);

        DelayTaxiParameters params = new DelayTaxiParameters(graph, delayValue, labelPropertyMapping);

        return new DelayTaxiProgram(params, new FlinkDeploymentConfig(Config.JAR_FILE, Config.FLINK_HOST, Config.FLINK_PORT));

//        return new DelayProgram(params);
    }
}
