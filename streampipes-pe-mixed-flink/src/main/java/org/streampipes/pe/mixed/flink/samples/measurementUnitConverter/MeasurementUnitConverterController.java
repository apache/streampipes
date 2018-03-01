package org.streampipes.pe.mixed.flink.samples.measurementUnitConverter;

import com.github.jqudt.Unit;
import org.streampipes.container.api.ResolvesContainerProvidedOptions;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.runtime.RuntimeOptions;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.sdk.helpers.TransformOperations;
import org.streampipes.units.UnitProvider;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MeasurementUnitConverterController extends
        FlinkDataProcessorDeclarer<MeasurementUnitConverterParameters> implements ResolvesContainerProvidedOptions {

    private static final String CONVERT_PROPERTY = "convert-property";
    private static final String OUTPUT_UNIT = "outputUnit";

    @Override
    public DataProcessorDescription declareModel() {


        return ProcessingElementBuilder.create("measurement_unit_converter", "Measurement Unit Converter",
                "Converts a unit of measurement to another one")
                .requiredStream(StreamRequirementsBuilder
                        .create()
                        .requiredPropertyWithUnaryMapping(EpRequirements.anyProperty(), Labels.from
                                (CONVERT_PROPERTY,"Property", "The" +
                                " property to convert"), PropertyScope.MEASUREMENT_PROPERTY)
                        .build())
                .requiredSingleValueSelectionFromContainer(Labels.from(OUTPUT_UNIT, "The output type unit of " +
                        "measurement", ""), "convert-property")
                .supportedProtocols(SupportedProtocols.kafka())
                .supportedFormats(SupportedFormats.jsonFormat())
                .outputStrategy(OutputStrategies.transform(TransformOperations
                        .dynamicMeasurementUnitTransformation(CONVERT_PROPERTY, OUTPUT_UNIT)))
                .build();
    }



    @Override
    public FlinkDataProcessorRuntime<MeasurementUnitConverterParameters> getRuntime(DataProcessorInvocation sepa, ProcessingElementParameterExtractor extractor) {

        String convertProperty = extractor.mappingPropertyValue(CONVERT_PROPERTY);
        String inputUnitId = extractor.measurementUnit(convertProperty, 0);
        String outputUnitId =  extractor.selectedSingleValue(OUTPUT_UNIT, String.class);

        Unit inputUnit = UnitProvider.INSTANCE.getUnit(inputUnitId);
        Unit outputUnit = UnitProvider.INSTANCE.getUnit(outputUnitId);

        MeasurementUnitConverterParameters staticParams = new MeasurementUnitConverterParameters(
                sepa,
                convertProperty,
                inputUnit,
                outputUnit
        );

        return new MeasurementUnitConverterProgram(staticParams,  new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
                FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
        //return new MeasurementUnitConverterProgram(staticParams);
    }


    @Override
    public List<RuntimeOptions> resolveOptions(String requestId, EventProperty linkedEventProperty) {
        if (linkedEventProperty instanceof EventPropertyPrimitive && ((EventPropertyPrimitive) linkedEventProperty)
                .getMeasurementUnit() != null) {
            Unit measurementUnit = UnitProvider.INSTANCE.getUnit(((EventPropertyPrimitive) linkedEventProperty)
                    .getMeasurementUnit().toString());
            URI type = measurementUnit.getType();
            List<Unit> availableUnits = UnitProvider.INSTANCE.getUnitsByType(type);
            return availableUnits.stream().filter(unit -> !(unit.getResource().toString().equals(measurementUnit
                    .getResource().toString()))).map
                    (unit -> new
                    RuntimeOptions
                    (unit
                    .getLabel(), unit
                    .getResource()
                    .toString()))
                    .collect(Collectors
                    .toList());
        } else {
            return new ArrayList<>();
        }
    }
}
