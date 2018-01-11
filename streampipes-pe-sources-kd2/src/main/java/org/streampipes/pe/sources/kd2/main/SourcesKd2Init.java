package org.streampipes.pe.sources.kd2.main;

import org.streampipes.container.embedded.init.ContainerModelSubmitter;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.pe.sources.kd2.config.SourcesConfig;
import org.streampipes.pe.sources.kd2.sources.BiodataSource;

public class SourcesKd2Init extends ContainerModelSubmitter {

    public void init() {

        DeclarersSingleton.getInstance().setRoute("sources-kd2");
        DeclarersSingleton.getInstance()
                .add(new BiodataSource());

        new SourcesKd2Init().init(SourcesConfig.INSTANCE);
    }

}
