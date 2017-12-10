package org.streampipes.pe.sources.demonstrator.sources;


import org.streampipes.container.embedded.init.ContainerModelSubmitter;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.pe.sources.demonstrator.config.DemonstratorConfig;

public class SourcesDemonstratorInit extends ContainerModelSubmitter {

    public void init() {
        DeclarersSingleton.getInstance().setRoute("sources-demonstrator");
        DeclarersSingleton.getInstance()
                .add(new SiemensProducer())
                .add(new FestoProducer());


        DeclarersSingleton.getInstance().setPort(DemonstratorConfig.INSTANCE.getPort());
        DeclarersSingleton.getInstance().setHostName(DemonstratorConfig.INSTANCE.getHost());

        new SourcesDemonstratorInit().init(DemonstratorConfig.INSTANCE);

    }

}
