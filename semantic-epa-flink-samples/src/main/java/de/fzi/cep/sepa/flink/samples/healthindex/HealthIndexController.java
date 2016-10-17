package de.fzi.cep.sepa.flink.samples.healthindex;

import de.fzi.cep.sepa.client.util.StandardTransportFormat;
import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.samples.Config;
import de.fzi.cep.sepa.model.builder.EpRequirements;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.MhWirth;
import de.fzi.cep.sepa.model.vocabulary.XSD;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 17.10.2016.
 */
public class HealthIndexController extends AbstractFlinkAgentDeclarer<HealthIndexParameters> {

    @Override
    public SepaDescription declareModel() {
        List<EventProperty> eventProperties = new ArrayList<EventProperty>();
        eventProperties.add(EpRequirements.domainPropertyReq(MhWirth.Stddev));
        eventProperties.add(EpRequirements.domainPropertyReq(MhWirth.FrictionValue));
        eventProperties.add(EpRequirements.domainPropertyReq(MhWirth.zScore));

        EventSchema schema1 = new EventSchema();
        schema1.setEventProperties(eventProperties);

        EventStream stream1 = new EventStream();
        stream1.setEventSchema(schema1);

        SepaDescription desc = new SepaDescription("health_index", "Health Index", "Calculates the health index based on swivel or gearbox friction coefficients.");

        desc.addEventStream(stream1);

        List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
        AppendOutputStrategy outputStrategy = new AppendOutputStrategy();

        List<EventProperty> appendProperties = new ArrayList<EventProperty>();
        appendProperties.add(new EventPropertyPrimitive(XSD._double.toString(),
                "healthIndex", "", de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number")));

        outputStrategy.setEventProperties(appendProperties);
        strategies.add(outputStrategy);
        desc.setOutputStrategies(strategies);
        desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());

        return desc;
    }

    @Override
    protected FlinkSepaRuntime<HealthIndexParameters> getRuntime(SepaInvocation graph) {
        AppendOutputStrategy strategy = (AppendOutputStrategy) graph.getOutputStrategies().get(0);

        String zScoreMapping = SepaUtils.getMappingPropertyName(graph, "zScore");
        String stddevMapping = SepaUtils.getMappingPropertyName(graph, "stddev");
        String frictionMapping = SepaUtils.getMappingPropertyName(graph, "friction");

        List<String> selectProperties = new ArrayList<>();
        for(EventProperty p : graph.getInputStreams().get(0).getEventSchema().getEventProperties())
        {
            selectProperties.add(p.getRuntimeName());
        }

        HealthIndexParameters staticParam = new HealthIndexParameters (
                graph,
                zScoreMapping,
                frictionMapping,
                stddevMapping);

        return new HealthIndexProgram(staticParam, new FlinkDeploymentConfig(Config.JAR_FILE, Config.FLINK_HOST, Config.FLINK_PORT));

    }
}
