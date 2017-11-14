package org.streampipes.pe.mixed.flink.samples.enrich.value;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.vocabulary.SO;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;

public class ValueController extends AbstractFlinkAgentDeclarer<ValueParameters> {


    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("enrich_value", "Flink Value Enrichment",
                "Appends a static value to the event payload using Flink")
                .requiredPropertyStream1(EpRequirements.anyProperty())
                .outputStrategy(OutputStrategies.append(
                        EpProperties.stringEp(Labels.empty(), "value", SO.Text)
                ))
                .requiredTextParameter("valueName", "Key name", "The name of the key")
                .requiredTextParameter("value", "The Value", "")
                .supportedProtocols(SupportedProtocols.kafka())
                .supportedFormats(SupportedFormats.jsonFormat())
                .build();
    }



    @Override
    protected FlinkSepaRuntime<ValueParameters> getRuntime(DataProcessorInvocation sepa) {
        ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(sepa);

        String valueName = extractor.singleValueParameter("valueName", String.class);
        String value = extractor.singleValueParameter("value", String.class);

        ValueParameters staticParam = new ValueParameters(
                sepa,
                valueName,
                value
        );

        return new ValueProgram(staticParam,  new FlinkDeploymentConfig(FlinkConfig.JAR_FILE, FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
        //return new ValueProgram(staticParam);
    }


}
