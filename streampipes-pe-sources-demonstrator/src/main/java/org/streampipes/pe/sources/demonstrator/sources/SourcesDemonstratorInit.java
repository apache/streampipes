package org.streampipes.pe.sources.demonstrator.sources;


import org.streampipes.container.embedded.init.ContainerModelSubmitter;
import org.streampipes.container.init.DeclarersSingleton;

    public class SourcesDemonstratorInit extends ContainerModelSubmitter {

    @Override
    public void init() {
        DeclarersSingleton.getInstance().setRoute("sources-demonstrator");
        DeclarersSingleton.getInstance()
                .add(new SiemensProducer())
                .add(new FestoProducer());

    }

}
