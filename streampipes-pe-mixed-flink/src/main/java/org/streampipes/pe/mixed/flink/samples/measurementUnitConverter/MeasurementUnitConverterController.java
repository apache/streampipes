package org.streampipes.pe.mixed.flink.samples.measurementUnitConverter;

import com.github.jqudt.Unit;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.units.UnitProvider;
import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.model.staticproperty.Option;

import java.util.LinkedList;
import java.util.List;

public class MeasurementUnitConverterController extends AbstractFlinkAgentDeclarer<MeasurementUnitConverterParameters> {


    @Override
    public DataProcessorDescription declareModel() {
        List<Unit> availableUnits = UnitProvider.INSTANCE.getAvailableUnits();
        List<Option> optionsList = new LinkedList<>();
        availableUnits.forEach(unit -> optionsList.add(new Option(unit.getLabel())));


        return ProcessingElementBuilder.create("measurement_unit_converter", "Measurement Unit Converter",
                "Converts a unit of measurement to another one")
                .requiredPropertyStream1(EpRequirements.anyProperty())
                .requiredTextParameter("unitName", "Unit name",
                        "The name of the unit which should convert")
                .requiredSingleValueSelection("inputUnit", "Input type",
                        "The input type unit of measurement", optionsList)
                .requiredSingleValueSelection("outputUnity", "Output type",
                        "The output type unit of measurement", optionsList)
                .supportedProtocols(SupportedProtocols.kafka())
                .supportedFormats(SupportedFormats.jsonFormat())
                .build();
    }



    @Override
    protected FlinkSepaRuntime<MeasurementUnitConverterParameters> getRuntime(DataProcessorInvocation sepa) {
        ProcessingElementParameterExtractor extractor = ProcessingElementParameterExtractor.from(sepa);

        String unitName = extractor.singleValueParameter("unityName", String.class);
        Option inputUnitOption = extractor.selectedSingleValue("inputUnit", Option.class);
        Option outputUnityOption =  extractor.selectedSingleValue("outputUnit", Option.class);

        Unit inputUnit = UnitProvider.INSTANCE.getUnitByLabel(inputUnitOption.getName());
        Unit outputUnit = UnitProvider.INSTANCE.getUnitByLabel(outputUnityOption.getName());


        MeasurementUnitConverterParameters staticParams = new MeasurementUnitConverterParameters(
                sepa,
                unitName,
                inputUnit,
                outputUnit
        );


        //return new MeasurementUnitConverterProgram(staticParams,  new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
        //        FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
        return new MeasurementUnitConverterProgram(staticParams);
    }


}
