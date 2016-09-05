package de.fzi.cep.sepa.sources.mhwirth.main;

import de.fzi.cep.sepa.client.container.init.ContainerModelSubmitter;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.sources.mhwirth.ddm.DDMProducer;
import de.fzi.cep.sepa.sources.mhwirth.drillbit.DrillBitProducer;
import de.fzi.cep.sepa.sources.mhwirth.enriched.EnrichedEventProducer;
import de.fzi.cep.sepa.sources.mhwirth.ram.RamProducer;


    public class SourcesMhwirthInit extends ContainerModelSubmitter {

    public void init() {

        DeclarersSingleton.getInstance().setRoute("sources-mhwirth");
        DeclarersSingleton.getInstance()
            .add(new DDMProducer())
            .add(new DrillBitProducer())
            .add(new EnrichedEventProducer())
            .add(new RamProducer());
    }

}
