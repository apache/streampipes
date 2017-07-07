package org.streampipes.pe.proasense.monitoring.main;

import org.streampipes.container.embedded.init.ContainerModelSubmitter;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.pe.proasense.monitoring.sources.MonitoringProducer;

public class SourcesMonitoringProasenseInit extends ContainerModelSubmitter {

    public void init() {

        DeclarersSingleton.getInstance().setRoute("sources-monitoring");
        DeclarersSingleton.getInstance()
                .add(new MonitoringProducer());

    }
}
