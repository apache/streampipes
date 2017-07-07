package org.streampipes.pe.algorithms.standalone.main;

import org.streampipes.pe.algorithms.standalone.languagedetection.LanguageDetectionController;
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
