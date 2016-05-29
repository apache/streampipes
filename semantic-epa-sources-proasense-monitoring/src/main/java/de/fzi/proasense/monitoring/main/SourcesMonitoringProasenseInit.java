package de.fzi.proasense.monitoring.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.client.container.init.ContainerModelSubmitter;
import de.fzi.cep.sepa.client.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.proasense.monitoring.sources.MonitoringProducer;

public class SourcesMonitoringProasenseInit extends ContainerModelSubmitter {

    public void init() {

        DeclarersSingleton.getInstance().setRoute("sources-monitoring");
        DeclarersSingleton.getInstance()
                .add(new MonitoringProducer());

    }
}
