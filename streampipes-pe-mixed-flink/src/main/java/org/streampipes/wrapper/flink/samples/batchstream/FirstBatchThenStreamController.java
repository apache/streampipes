package org.streampipes.wrapper.flink.samples.batchstream;

import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.wrapper.flink.samples.FlinkConfig;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;


public class FirstBatchThenStreamController extends AbstractFlinkAgentDeclarer<FirstBatchThenStreamParameters> {

    @Override
    public SepaDescription declareModel() {
        SepaDescription fbtsDescription = ProcessingElementBuilder
                .create("fbts", "First Batch Then Stream", "This element combines two data streams into one. The " +
                        "first input must be a bounded data set and the second must be an unbounded data set. " +
                        "First the bounded data set is put on the result stream, then the real time data is put " +
                        "on the stream")
                .iconUrl("url")
                .setStream1()
                .setStream2()
//                .requiredPropertyStream1(EpRequirements.anyProperty())
//                .requiredPropertyStream2(EpRequirements.anyProperty())
                .supportedFormats(SupportedFormats.jsonFormat())
                .supportedProtocols(SupportedProtocols.kafka())
                .outputStrategy(OutputStrategies.keep(false))
                .build();

        return fbtsDescription;
    }


    @Override
    protected FlinkSepaRuntime<FirstBatchThenStreamParameters> getRuntime(SepaInvocation graph) {

        FirstBatchThenStreamParameters params = new FirstBatchThenStreamParameters(graph);

        return new FirstBatchThenStreamProgram(params, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE, FlinkConfig.FLINK_HOST, FlinkConfig.FLINK_PORT));

//        return new FirstBatchThenStreamProgram(params);

    }
}
