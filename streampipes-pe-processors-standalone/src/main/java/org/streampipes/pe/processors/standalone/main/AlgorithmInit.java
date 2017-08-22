package org.streampipes.pe.processors.standalone.main;

import org.streampipes.pe.processors.standalone.languagedetection.LanguageDetectionController;
import org.streampipes.container.embedded.init.ContainerModelSubmitter;
import org.streampipes.container.init.DeclarersSingleton;

public class AlgorithmInit extends ContainerModelSubmitter{

	@Override
	public void init() {
		DeclarersSingleton.getInstance().setRoute("algorithms");
        DeclarersSingleton.getInstance()
                .add(new LanguageDetectionController());
	}
}
