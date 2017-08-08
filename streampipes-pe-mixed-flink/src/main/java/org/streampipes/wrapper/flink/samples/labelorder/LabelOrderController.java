package org.streampipes.wrapper.flink.samples.labelorder;

import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.wrapper.flink.samples.FlinkConfig;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.vocabulary.XSD;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.commons.Utils;

public class LabelOrderController extends AbstractFlinkAgentDeclarer<LabelOrderParameters> {

    @Override
    public SepaDescription declareModel() {
        SepaDescription delayDescription = ProcessingElementBuilder
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
    protected FlinkSepaRuntime<LabelOrderParameters> getRuntime(SepaInvocation graph) {

        LabelOrderParameters params = new LabelOrderParameters(graph);

        return new LabelOrderProgram(params, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE, FlinkConfig.FLINK_HOST, FlinkConfig.FLINK_PORT));
//
//        return new LabelOrderProgram(params);
    }
}
