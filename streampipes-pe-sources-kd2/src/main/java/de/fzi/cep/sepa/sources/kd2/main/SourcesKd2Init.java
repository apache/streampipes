package de.fzi.cep.sepa.sources.kd2.main;

import de.fzi.cep.sepa.client.container.init.ContainerModelSubmitter;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.sources.kd2.sources.BiodataSource;

/**
 * Created by riemer on 18.11.2016.
 */
public class SourcesKd2Init extends ContainerModelSubmitter {
    @Override
    public void init() {

        DeclarersSingleton.getInstance().setRoute("sources-kd2");
        DeclarersSingleton.getInstance()
                .add(new BiodataSource());
    }

}
