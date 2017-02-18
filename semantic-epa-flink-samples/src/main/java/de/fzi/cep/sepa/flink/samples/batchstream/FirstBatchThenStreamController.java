package de.fzi.cep.sepa.flink.samples.batchstream;

import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.helpers.OutputStrategies;
import de.fzi.cep.sepa.sdk.helpers.SupportedFormats;
import de.fzi.cep.sepa.sdk.helpers.SupportedProtocols;

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
        return null;
    }
}
