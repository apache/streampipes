package de.fzi.cep.sepa.manager.kpi.pipelineelements;

import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.DomainStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;

/**
 * Created by riemer on 03.10.2016.
 */
public class KafkaPublisherGenerator extends PipelineElementGenerator<SecInvocation, KafkaSettings> {

    private static final String KAFKA_HOST_PROPERTY = "http://schema.org/kafkaHost";
    private static final String KAFKA_PORT_PROPERTY = "http://schema.org/kafkaPort";


    public KafkaPublisherGenerator(SecInvocation pipelineElement, KafkaSettings kafkaSettings) {
        super(pipelineElement, kafkaSettings);
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
                .findFirst()
                .get()
                .setValue(settings.getTopic());

        return pipelineElement;
    }
}
