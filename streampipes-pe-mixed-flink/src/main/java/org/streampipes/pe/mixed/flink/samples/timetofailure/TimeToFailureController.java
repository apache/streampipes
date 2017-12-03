package org.streampipes.pe.mixed.flink.samples.timetofailure;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.output.AppendOutputStrategy;
import org.streampipes.model.output.OutputStrategy;
import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.MappingProperty;
import org.streampipes.model.staticproperty.MappingPropertyUnary;
import org.streampipes.model.staticproperty.PropertyValueSpecification;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.vocabulary.MhWirth;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 26.10.2016.
 */
public class TimeToFailureController extends FlinkDataProcessorDeclarer<TimeToFailureParameters> {

    private final String healthIndexMappingName = "healthIndexMappingName";
    private final String mtbf = "mtbf";


    @Override
    public DataProcessorDescription declareModel() {
        List<EventProperty> eventProperties = new ArrayList<EventProperty>();

        EventProperty healthIndexRequirement = EpRequirements.domainPropertyReq(MhWirth.HealthIndex);
        eventProperties.add(healthIndexRequirement);

        EventSchema schema1 = new EventSchema();
        schema1.setEventProperties(eventProperties);

        SpDataStream stream1 = new SpDataStream();
        stream1.setEventSchema(schema1);

        DataProcessorDescription desc = new DataProcessorDescription("time_to_failure", "Time to Failure", "Calculates the time to failure based on the health index.");

        desc.addEventStream(stream1);

        List<StaticProperty> staticProperties = new ArrayList<>();

        MappingProperty frictionValueMapping = new MappingPropertyUnary(URI.create(healthIndexRequirement.getElementId()), healthIndexMappingName, "Health Index Mapping", "The field containing health index values.");

        staticProperties.add(frictionValueMapping);
        FreeTextStaticProperty mtbfSp = StaticProperties.integerFreeTextProperty(mtbf, "MTBF (years)", "");
        mtbfSp.setValue("20");
        mtbfSp.setValueSpecification(new PropertyValueSpecification(1, 50, 1));
        staticProperties.add(mtbfSp);

        desc.setStaticProperties(staticProperties);

        List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
        AppendOutputStrategy outputStrategy = new AppendOutputStrategy();

        List<EventProperty> outputProperties = new ArrayList<EventProperty>();
        outputProperties.add(EpProperties.doubleEp(Labels.empty(), "ttf", MhWirth.Ttf));

        outputStrategy.setEventProperties(outputProperties);
        strategies.add(outputStrategy);
        desc.setOutputStrategies(strategies);
        desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());

        return desc;
    }

    @Override
    protected FlinkDataProcessorRuntime<TimeToFailureParameters> getRuntime(DataProcessorInvocation graph) {
        String healthIndexMapping = SepaUtils.getMappingPropertyName(graph, healthIndexMappingName);
        Integer mtbfValue = Integer.parseInt(SepaUtils.getFreeTextStaticPropertyValue(graph, mtbf));

        TimeToFailureParameters params = new TimeToFailureParameters(graph, healthIndexMapping, mtbfValue);

        return new TimeToFailureProgram(params, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
                FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
        //return new TimeToFailureProgram(params);

    }

}
