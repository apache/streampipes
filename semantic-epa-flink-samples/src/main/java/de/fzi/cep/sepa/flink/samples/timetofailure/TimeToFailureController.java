package de.fzi.cep.sepa.flink.samples.timetofailure;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.model.impl.staticproperty.*;
import de.fzi.cep.sepa.sdk.helpers.EpProperties;
import de.fzi.cep.sepa.sdk.helpers.EpRequirements;
import de.fzi.cep.sepa.sdk.StaticProperties;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.MhWirth;

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

        return new TimeToFailureProgram(params, new FlinkDeploymentConfig(Config.JAR_FILE, Config.FLINK_HOST, Config.FLINK_PORT));
        //return new TimeToFailureProgram(params);

    }

}
