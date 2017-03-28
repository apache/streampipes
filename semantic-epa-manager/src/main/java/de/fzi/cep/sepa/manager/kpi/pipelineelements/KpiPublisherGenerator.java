package de.fzi.cep.sepa.manager.kpi.pipelineelements;

import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.DomainStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;

/**
 * Created by riemer on 11.10.2016.
 */
public class KpiPublisherGenerator extends PipelineElementGenerator<SecInvocation, KpiPublisherSettings> {

    private static final String KAFKA_HOST_PROPERTY = "http://schema.org/kafkaHost";
    private static final String KAFKA_PORT_PROPERTY = "http://schema.org/kafkaPort";


    public KpiPublisherGenerator(SecInvocation pipelineElement, KpiPublisherSettings settings) {
        super(pipelineElement, settings);
    }

    @Override
    public SecInvocation makeInvocationGraph() {

        pipelineElement
                .getStaticProperties()
                .stream()
                .filter(s -> s instanceof DomainStaticProperty)
                .map(s -> domainStaticProperty(s))
                .findFirst()
                .get()
                .getSupportedProperties()
                .forEach(sp -> {
                    if (sp.getPropertyId().equals(KAFKA_HOST_PROPERTY)) {
                        sp.setValue(settings.getKafkaHost());
                    } else if (sp.getPropertyId().equals(KAFKA_PORT_PROPERTY)) {
                        sp.setValue(settings.getKafkaPort());
                    }
                });

        pipelineElement
                .getStaticProperties()
                .stream()
                .filter(s -> s instanceof FreeTextStaticProperty)
                .map(s -> freeTextStaticProperty(s))
                .forEach(sp -> {
                    if (sp.getInternalName().equals("topic")) {
                        sp.setValue(settings.getTopic());
                    } else if (sp.getInternalName().equals("kpi")) {
                        sp.setValue(settings.getKpiId());
                    }
                });

        return pipelineElement;
    }
}
