package org.apache.streampipes.processors.filters.jvm.processor.schema;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.List;

public class MergeBySchemaController extends StandaloneEventProcessingDeclarer<MergeBySchemaParameters> {


    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("org.apache.streampipes.processors.filters.jvm.schema")
                .category(DataProcessorType.TRANSFORM)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .withLocales(Locales.EN)
                .outputStrategy(OutputStrategies.custom(true))
                .build();
    }

    @Override
    public ConfiguredEventProcessor<MergeBySchemaParameters>
    onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

        List<String> outputKeySelectors = extractor.outputKeySelectors();

        MergeBySchemaParameters staticParam = new MergeBySchemaParameters(
                graph, outputKeySelectors);

        return new ConfiguredEventProcessor<>(staticParam, MergeBySchema::new);
    }
}
