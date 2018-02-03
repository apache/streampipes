package org.streampipes.pe.mixed.spark.samples.enrich.timestamp;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.pe.mixed.spark.SparkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.helpers.*;
import org.streampipes.vocabulary.SO;
import org.streampipes.wrapper.spark.SparkDataProcessorDeclarer;
import org.streampipes.wrapper.spark.SparkDataProcessorRuntime;
import org.streampipes.wrapper.spark.SparkDeploymentConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jochen Lutz on 2018-01-22.
 */
public class TimestampController extends SparkDataProcessorDeclarer<TimestampParameters> {
    public org.streampipes.model.graph.DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("enrich_configurable_timestamp", "Configurable Spark Timestamp Enrichment",
                "Appends the current time in ms to the event payload using Spark")
                .iconUrl(SparkConfig.getIconUrl("enrich-timestamp-icon"))
                .requiredPropertyStream1(EpRequirements.anyProperty())
                .outputStrategy(OutputStrategies.append(
                        EpProperties.longEp(Labels.empty(), "appendedTime", SO.DateTime)))
                .supportedProtocols(SupportedProtocols.kafka())
                .supportedFormats(SupportedFormats.jsonFormat())
                .build();
    }

    protected SparkDataProcessorRuntime getRuntime(DataProcessorInvocation dataProcessorInvocation) {
        AppendOutputStrategy strategy = (AppendOutputStrategy) dataProcessorInvocation.getOutputStrategies().get(0);

        String appendTimePropertyName = SepaUtils.getEventPropertyName(strategy.getEventProperties(), "appendedTime");

        List<String> selectProperties = new ArrayList<>();
        for (EventProperty p : dataProcessorInvocation.getInputStreams().get(0).getEventSchema().getEventProperties()) {
            selectProperties.add(p.getRuntimeName());
        }

        TimestampParameters staticParam = new TimestampParameters(
                dataProcessorInvocation,
                appendTimePropertyName,
                selectProperties);//TODO

        return new TimestampProgram(staticParam, new SparkDeploymentConfig(SparkConfig.JAR_FILE,
                SparkConfig.INSTANCE.getAppName(),
                SparkConfig.INSTANCE.getSparkHost(),
                SparkConfig.INSTANCE.getSparkBatchDuration(),
                SparkConfig.INSTANCE.getKafkaHostPort()));
    }
}
