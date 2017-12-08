package org.streampipes.pe.mixed.flink.samples.measurementUnitConverter;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;

public class MeasurementUnitConverterController extends AbstractFlinkAgentDeclarer<MeasurementUnitConverterParameters> {


    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("measurement_unit_converter", "Measurement Unit Converter",
                "Converts a unit of measurement to another one")

                .supportedProtocols(SupportedProtocols.kafka())
                .supportedFormats(SupportedFormats.jsonFormat())
                .build();
    }



    @Override
    protected FlinkSepaRuntime<MeasurementUnitConverterParameters> getRuntime(DataProcessorInvocation sepaInvocation) {

        MeasurementUnitConverterParameters staticParams = new MeasurementUnitConverterParameters(sepaInvocation);


        return new MeasurementUnitConverterProgram(staticParams,  new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
                FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
        //return new ProcessorTemplateProgram(staticParams);
    }


}
