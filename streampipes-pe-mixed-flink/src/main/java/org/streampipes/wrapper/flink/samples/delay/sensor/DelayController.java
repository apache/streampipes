package org.streampipes.wrapper.flink.samples.delay.sensor;

import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.wrapper.flink.samples.FlinkConfig;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.commons.Utils;

public class DelayController extends AbstractFlinkAgentDeclarer<DelayParameters> {

    public static String OUTPUT_LABEL = "delay_label";
    private static String DELAY_VALUE_NAME = "delay_value";
    private static String LABEL_PROPERTY_NAME = "label_property";


    @Override
    public SepaDescription declareModel() {
        SepaDescription delayDescription = ProcessingElementBuilder
                .create("delay", "Delay", "This SEPA hands the event without the label to the next component " +
                        "and stores it in kafka. Once the delay time is passed it reads the event from kafka " +
                        "and adds the correct value of the label to the event and passes the new event to the " +
                        "next component")
                .iconUrl(FlinkConfig.getIconUrl("delay-icon"))
                .supportedProtocols(SupportedProtocols.kafka())
                .supportedFormats(SupportedFormats.jsonFormat())
                .requiredIntegerParameter(DELAY_VALUE_NAME, "Delay Value [min]", "Minutes till the correct label is knonwn")
                .stream1PropertyRequirementWithUnaryMapping(EpRequirements.numberReq(), LABEL_PROPERTY_NAME,
                        "Label Property", "The property that is selected for the label")
                .outputStrategy(OutputStrategies.append(new EventPropertyPrimitive(
                        XSD._long.toString(), OUTPUT_LABEL, "",
                        Utils.createURI("http://schema.org/Number"))
                ))
                .build();

        return delayDescription;
    }

    @Override
    protected FlinkSepaRuntime<DelayParameters> getRuntime(SepaInvocation graph) {

        int delayValue = Integer.parseInt(SepaUtils.getFreeTextStaticPropertyValue(graph, DELAY_VALUE_NAME));
        String labelPropertyMapping = SepaUtils.getMappingPropertyName(graph, LABEL_PROPERTY_NAME);

        DelayParameters params = new DelayParameters(graph, delayValue, labelPropertyMapping);

        return new DelayProgram(params, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE, FlinkConfig.FLINK_HOST, FlinkConfig.FLINK_PORT));

//        return new DelayProgram(params);
    }
}
