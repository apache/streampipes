package de.fzi.proasense.demonstrator.sources;


import de.fzi.cep.sepa.client.container.init.ContainerModelSubmitter;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;

    public class SourcesDemonstratorInit extends ContainerModelSubmitter {

    @Override
    public void init() {
        DeclarersSingleton.getInstance().setRoute("sources-demonstrator");
        DeclarersSingleton.getInstance()
                .add(new SiemensProducer())
                .add(new FestoProducer());

    }

}
