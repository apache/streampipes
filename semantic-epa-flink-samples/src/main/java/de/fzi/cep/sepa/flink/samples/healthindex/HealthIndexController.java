package de.fzi.cep.sepa.flink.samples.healthindex;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.model.builder.EpProperties;
import de.fzi.cep.sepa.model.builder.EpRequirements;
import de.fzi.cep.sepa.model.builder.StaticProperties;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.FixedOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.MhWirth;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 17.10.2016.
 */
public class HealthIndexController extends AbstractFlinkAgentDeclarer<HealthIndexParameters> {

    private final String frictionCoefficientNominal = "frictionCoefficientNominal";
    private final String frictionCoefficientStdDev = "frictionCoefficientStdDev";
    private final String frictionCoefficientStdDevMultiplier = "frictionCoefficientStdDevMultiplier";

    private final String degradationRateBase = "degradationRateBase";
    private final String degradationRateDivider = "degradationRateDivider";
    private final String degradationValueMultiplier = "degradationvalueMultiplier";
    private final String degradationValueOffset = "degradationValueOffset";

    private final String frictionMappingName = "frictionMapping";
    private final String timestampMappingName = "timestampMapping";
    private final String machineTypeMappingName = "machineTypeMapping";
    private final String frictionCoefficientDegradationRate = "frictioNCoefficientDegradationRate";


    @Override
    public SepaDescription declareModel() {
        List<EventProperty> eventProperties = new ArrayList<EventProperty>();

        EventProperty frictionPropertyRequirement = EpRequirements.domainPropertyReq(MhWirth.FrictionValue);
        EventProperty timestampRequirement = EpRequirements.domainPropertyReq("http://schema.org/DateTime");
        EventProperty machineTypeRequirement = EpRequirements.stringReq();

        eventProperties.add(EpRequirements.domainPropertyReq(MhWirth.Stddev));
        eventProperties.add(frictionPropertyRequirement);
        eventProperties.add(timestampRequirement);
        eventProperties.add(machineTypeRequirement);

        EventSchema schema1 = new EventSchema();
        schema1.setEventProperties(eventProperties);

        EventStream stream1 = new EventStream();
        stream1.setEventSchema(schema1);

        SepaDescription desc = new SepaDescription("health_index", "Health Index", "Calculates the health index based on swivel or gearbox friction coefficients.");

        desc.addEventStream(stream1);

        List<StaticProperty> staticProperties = new ArrayList<>();

        MappingProperty frictionValueMapping = new MappingPropertyUnary(URI.create(frictionPropertyRequirement.getElementId()), frictionMappingName, "Friction Coefficient Mapping", "The field containing friction coefficient values.");
        MappingProperty timestampMapping = new MappingPropertyUnary(URI.create(timestampRequirement.getElementId()), timestampMappingName, "Timestamp Mapping", "The field containing the current timestamp.");
        MappingProperty machineTypeMapping = new MappingPropertyUnary(URI.create(machineTypeRequirement.getElementId()), machineTypeMappingName, "Machine Type Mapping", "The field containing an identifier of a machine or a part(e.g., swivel");

        staticProperties.add(frictionValueMapping);
        staticProperties.add(timestampMapping);
        staticProperties.add(machineTypeMapping);

        staticProperties.add(StaticProperties.doubleFreeTextProperty(frictionCoefficientNominal, "Nominal Friction Coefficient (sigma_f)", ""));
        staticProperties.add(StaticProperties.doubleFreeTextProperty(frictionCoefficientStdDev, "Friction Coefficient standard deviation", ""));
        staticProperties.add(StaticProperties.integerFreeTextProperty(frictionCoefficientStdDevMultiplier, "Multiplier delta_cx: gamma = delta_cx * sigma_f", ""));

        staticProperties.add(StaticProperties.integerFreeTextProperty(degradationRateBase, "Degradation Rate Base (Power)", ""));
        staticProperties.add(StaticProperties.integerFreeTextProperty(degradationRateDivider, "Degradation Rate Divider", ""));

        desc.setStaticProperties(staticProperties);

        List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
        FixedOutputStrategy outputStrategy = new FixedOutputStrategy();

        List<EventProperty> outputProperties = new ArrayList<EventProperty>();
        outputProperties.add(EpProperties.doubleEp("healthIndex", MhWirth.HealthIndex));
        outputProperties.add(EpProperties.longEp("timestamp", "http://schema.org/DateTime"));
        outputProperties.add(EpProperties.stringEp("machineId", MhWirth.MachineId));

        outputStrategy.setEventProperties(outputProperties);
        strategies.add(outputStrategy);
        desc.setOutputStrategies(strategies);
        desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());

        return desc;
    }

    @Override
    protected FlinkSepaRuntime<HealthIndexParameters> getRuntime(SepaInvocation graph) {

        String frictionMapping = SepaUtils.getMappingPropertyName(graph, frictionMappingName);
        String timestampMapping = SepaUtils.getMappingPropertyName(graph, timestampMappingName);
        String machineTypeMapping = SepaUtils.getMappingPropertyName(graph, machineTypeMappingName);

        HealthIndexVariables2 variables = new HealthIndexVariables2();

        variables.setAverage(Double.parseDouble(SepaUtils.getFreeTextStaticPropertyValue(graph, frictionCoefficientNominal)));
        variables.setStddev(Double.parseDouble(SepaUtils.getFreeTextStaticPropertyValue(graph, frictionCoefficientStdDev)));

        variables.setDeltacx(Integer.parseInt(SepaUtils.getFreeTextStaticPropertyValue(graph, frictionCoefficientStdDevMultiplier)));

        variables.setPower(Integer.parseInt(SepaUtils.getFreeTextStaticPropertyValue(graph, degradationRateBase)));
        variables.setDivider(Integer.parseInt(SepaUtils.getFreeTextStaticPropertyValue(graph, degradationRateDivider)));

        HealthIndexParameters staticParam = new HealthIndexParameters (
                graph,
                frictionMapping,
                timestampMapping,
                machineTypeMapping,
                variables);

        return new HealthIndexProgram(staticParam, new FlinkDeploymentConfig(Config.JAR_FILE, Config.FLINK_HOST, Config.FLINK_PORT));

        //return new HealthIndexProgram(staticParam);
    }
}
