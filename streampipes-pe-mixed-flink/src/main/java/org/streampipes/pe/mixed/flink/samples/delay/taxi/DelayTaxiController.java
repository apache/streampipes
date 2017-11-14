package org.streampipes.pe.mixed.flink.samples.delay.taxi;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.vocabulary.SO;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.OutputStrategies;

public class DelayTaxiController extends AbstractFlinkAgentDeclarer<DelayTaxiParameters> {

    public static String OUTPUT_LABEL = "delay_label";
    private static String DELAY_VALUE_NAME = "delay_value";
    private static String LABEL_PROPERTY_NAME = "label_property";


    @Override
    public DataProcessorDescription declareModel() {
        DataProcessorDescription delayDescription = ProcessingElementBuilder
                .create("delay_taxi", "Delay Taxi", "Waits a configured time and adds the labeld to the correspondig grid cell to " +
                        "the event")
                .iconUrl("url")
                .supportedFormats(StandardTransportFormat.standardFormat())
                .supportedProtocols(StandardTransportFormat.standardProtocols())
                .requiredPropertyStream1WithUnaryMapping(EpRequirements.numberReq(), LABEL_PROPERTY_NAME,
                        "Label Property", "The property that is selected for the label")
                .outputStrategy(OutputStrategies.append(
                        EpProperties.integerEp(Labels.empty(), OUTPUT_LABEL, SO.Number)
                ))
                .build();

        return delayDescription;
    }

    @Override
    protected FlinkSepaRuntime<DelayTaxiParameters> getRuntime(DataProcessorInvocation graph) {

        String labelPropertyMapping = SepaUtils.getMappingPropertyName(graph, LABEL_PROPERTY_NAME);

        DelayTaxiParameters params = new DelayTaxiParameters(graph, labelPropertyMapping);

        return new DelayTaxiProgram(params, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
                FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
//        return new DelayTaxiProgram(params);
    }
}
