package org.streampipes.pe.mixed.flink.samples.timetofailure;

import org.streampipes.container.util.StandardTransportFormat;
import org.streampipes.wrapper.flink.AbstractFlinkAgentDeclarer;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.pe.mixed.flink.samples.FlinkConfig;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.AppendOutputStrategy;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.MappingProperty;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.PropertyValueSpecification;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.MhWirth;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 26.10.2016.
 */
public class TimeToFailureController extends AbstractFlinkAgentDeclarer<TimeToFailureParameters> {

    private final String healthIndexMappingName = "healthIndexMappingName";
    private final String mtbf = "mtbf";


    @Override
    public SepaDescription declareModel() {
        List<EventProperty> eventProperties = new ArrayList<EventProperty>();

        EventProperty healthIndexRequirement = EpRequirements.domainPropertyReq(MhWirth.HealthIndex);
        eventProperties.add(healthIndexRequirement);

        EventSchema schema1 = new EventSchema();
        schema1.setEventProperties(eventProperties);

        EventStream stream1 = new EventStream();
        stream1.setEventSchema(schema1);

        SepaDescription desc = new SepaDescription("time_to_failure", "Time to Failure", "Calculates the time to failure based on the health index.");

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
        outputProperties.add(EpProperties.doubleEp("ttf", MhWirth.Ttf));

        outputStrategy.setEventProperties(outputProperties);
        strategies.add(outputStrategy);
        desc.setOutputStrategies(strategies);
        desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());

        return desc;
    }

    @Override
    protected FlinkSepaRuntime<TimeToFailureParameters> getRuntime(SepaInvocation graph) {
        String healthIndexMapping = SepaUtils.getMappingPropertyName(graph, healthIndexMappingName);
        Integer mtbfValue = Integer.parseInt(SepaUtils.getFreeTextStaticPropertyValue(graph, mtbf));

        TimeToFailureParameters params = new TimeToFailureParameters(graph, healthIndexMapping, mtbfValue);

        return new TimeToFailureProgram(params, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE,
                FlinkConfig.INSTANCE.getFlinkHost(), FlinkConfig.INSTANCE.getFlinkPort()));
        //return new TimeToFailureProgram(params);

    }

}
