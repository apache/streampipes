package org.streampipes.pe.sources.mhwirth.main;

import org.streampipes.container.embedded.init.ContainerModelSubmitter;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.pe.sources.mhwirth.config.SourcesConfig;
import org.streampipes.pe.sources.mhwirth.ddm.DDMProducer;
import org.streampipes.pe.sources.mhwirth.drillbit.DrillBitProducer;
import org.streampipes.pe.sources.mhwirth.enriched.EnrichedEventProducer;
import org.streampipes.pe.sources.mhwirth.ram.RamProducer;


public class SourcesMhwirthInit extends ContainerModelSubmitter {

    public void init() {

        DeclarersSingleton.getInstance().setRoute("sources-mhwirth");
        DeclarersSingleton.getInstance().setPort(SourcesConfig.INSTANCE.getPort());
        DeclarersSingleton.getInstance().setHostName(SourcesConfig.INSTANCE.getHost());

        DeclarersSingleton.getInstance()
            .add(new DDMProducer())
            .add(new DrillBitProducer())
            .add(new EnrichedEventProducer())
            .add(new RamProducer());
    }

}
