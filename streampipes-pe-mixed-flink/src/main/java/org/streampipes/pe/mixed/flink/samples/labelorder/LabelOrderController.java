package org.streampipes.pe.mixed.flink.samples.labelorder;

import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.vocabulary.XSD;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.commons.Utils;

public class LabelOrderController extends FlinkDataProcessorDeclarer<LabelOrderParameters> {

    @Override
    public DataProcessorDescription declareModel() {
        DataProcessorDescription delayDescription = ProcessingElementBuilder
                .create("labelorder", "Label Orders", "This component labels the orders with the time difference to" +
                        "the next maintenance step.")
                .iconUrl(FlinkConfig.getIconUrl("label-icon"))
                .supportedProtocols(SupportedProtocols.kafka())
                .supportedFormats(SupportedFormats.jsonFormat())
                //TODO add stream requirements
                .setStream1()
                .setStream2()
                .outputStrategy(OutputStrategies.append(new EventPropertyPrimitive(
                        XSD._long.toString(), "nextMaintenance", "",
                        Utils.createURI("http://schema.org/Number"))
                ))
                .build();

        return delayDescription;
    }

    @Override
    public FlinkDataProcessorRuntime<LabelOrderParameters> getRuntime(DataProcessorInvocation graph) {

        LabelOrderParameters params = new LabelOrderParameters(graph);

        return new LabelOrderProgram(params, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
                FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
//
//        return new LabelOrderProgram(params);
    }
}
