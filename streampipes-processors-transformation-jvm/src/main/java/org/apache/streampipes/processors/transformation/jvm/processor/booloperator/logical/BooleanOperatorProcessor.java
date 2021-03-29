package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.enums.BooleanOperatorType;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.operations.IBoolOperation;
import org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.operations.factory.BoolOperationFactory;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.standalone.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.util.List;

import static org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical.enums.BooleanOperatorType.NOT;

public class BooleanOperatorProcessor extends StreamPipesDataProcessor {

    private static final String BOOLEAN_PROCESSOR_INPUT_KEY = "boolean-processor-configs";
    private static final String BOOLEAN_PROCESSOR_OUT_KEY = "boolean-operations-result";
    private BooleanOperationInputConfigs configs;

    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.processor.booloperator.logical")
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .withLocales(Locales.EN)
                .category(DataProcessorType.ENRICH)
                .requiredStream(
                        StreamRequirementsBuilder
                                .create()
                                .requiredProperty(EpRequirements.anyProperty())
                                .build())
                .requiredTextParameter(Labels.withId(BOOLEAN_PROCESSOR_INPUT_KEY))
                .outputStrategy(OutputStrategies.append(
                        PrimitivePropertyBuilder.create(
                                Datatypes.String, BOOLEAN_PROCESSOR_OUT_KEY)
                                .build()))
                .build();
    }

    @Override
    public void onInvocation(ProcessorParams processorParams, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext eventProcessorRuntimeContext) throws SpRuntimeException {
        BooleanOperationInputConfigs configs = processorParams.extractor().singleValueParameter(BOOLEAN_PROCESSOR_INPUT_KEY, BooleanOperationInputConfigs.class);
        preChecks(configs);
        this.configs = configs;
    }

    @Override
    public void onEvent(Event event, SpOutputCollector spOutputCollector) throws SpRuntimeException {
        List<String> properties = configs.getProperties();
        BooleanOperatorType operatorType = configs.getOperator();
        Boolean firstProperty = event.getFieldBySelector(properties.get(0)).getAsPrimitive().getAsBoolean();
        IBoolOperation<Boolean> boolOperation = BoolOperationFactory.getBoolOperation(operatorType);
        Boolean result;
        if (properties.size() == 1) {
            // support for NOT operator
            result = boolOperation.evaluate(firstProperty, firstProperty);

        } else {
            Boolean secondProperty = event.getFieldBySelector(properties.get(1)).getAsPrimitive().getAsBoolean();
            result = boolOperation.evaluate(firstProperty, secondProperty);

            //loop through rest of the properties to get final result
            for (int i = 2; i < properties.size(); i++) {
                result = boolOperation.evaluate(result, event.getFieldBySelector(properties.get(i)).getAsPrimitive().getAsBoolean());
            }

        }
        event.addField(BOOLEAN_PROCESSOR_OUT_KEY, result);
        spOutputCollector.collect(event);
    }

    @Override
    public void onDetach() throws SpRuntimeException {
        configs = null;
    }

    private void preChecks(BooleanOperationInputConfigs configs) {
        BooleanOperatorType operatorType = configs.getOperator();
        List<String> properties =  configs.getProperties();
        if (operatorType == NOT) {
            assert properties.size() == 1 : "NOT operator can operate only on single operand";
        } else {
            assert properties.size() >= 2 : "Number of operands are less that 2";
        }
    }
}
