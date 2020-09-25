package org.apache.streampipes.processors.changedetection.jvm.cusum;

import java.util.Arrays;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class CusumController extends StandaloneEventProcessingDeclarer<CusumParameters> {

    private static final String NUMBER_MAPPING = "number-mapping";
    private static final String PARAM_K = "param-k";
    private static final String PARAM_H = "param-h";

    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("org.apache.streampipes.processors.changedetection.jvm.cusum")
                .category(DataProcessorType.FILTER)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .withLocales(Locales.EN)
                .requiredStream(StreamRequirementsBuilder
                        .create()
                        .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                                Labels.withId(NUMBER_MAPPING),
                                PropertyScope.NONE).build())
                .requiredFloatParameter(Labels.withId(PARAM_K), 0.0f, 0.0f, 100.0f, 0.01f)
                .requiredFloatParameter(Labels.withId(PARAM_H), 0.0f, 0.0f, 100.0f, 0.01f)
                .outputStrategy(
                        OutputStrategies.append(
                                Arrays.asList(
                                        EpProperties.numberEp(Labels.empty(), CusumEventFields.VAL_LOW, SO.Number),
                                        EpProperties.numberEp(Labels.empty(), CusumEventFields.VAL_HIGH, SO.Number),
                                        EpProperties.booleanEp(Labels.empty(), CusumEventFields.DECISION_LOW, SO.Boolean),
                                        EpProperties.booleanEp(Labels.empty(), CusumEventFields.DECISION_HIGH, SO.Boolean)
                                )
                        ))
                .build();
    }

    @Override
    public ConfiguredEventProcessor<CusumParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

        String selectedNumberField = extractor.mappingPropertyValue(NUMBER_MAPPING);
        Double paramK = extractor.singleValueParameter(PARAM_K, Double.class);
        Double paramH = extractor.singleValueParameter(PARAM_H, Double.class);

        CusumParameters params = new CusumParameters(graph, selectedNumberField, paramK, paramH);

        return new ConfiguredEventProcessor<>(params, Cusum::new);
    }
}
