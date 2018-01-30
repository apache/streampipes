package org.streampipes.pe.mixed.flink.samples.batchstream;

import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;


public class FirstBatchThenStreamController extends FlinkDataProcessorDeclarer<FirstBatchThenStreamParameters> {

    @Override
    public DataProcessorDescription declareModel() {
        DataProcessorDescription fbtsDescription = ProcessingElementBuilder
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
    public FlinkDataProcessorRuntime<FirstBatchThenStreamParameters> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

        FirstBatchThenStreamParameters params = new FirstBatchThenStreamParameters(graph);

        return new FirstBatchThenStreamProgram(params, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
                FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));

//        return new FirstBatchThenStreamProgram(params);

    }
}
