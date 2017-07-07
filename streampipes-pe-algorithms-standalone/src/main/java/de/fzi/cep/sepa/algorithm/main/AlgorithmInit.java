package de.fzi.cep.sepa.algorithm.main;

import de.fzi.cep.sepa.algorithm.languagedetection.LanguageDetectionController;
import de.fzi.cep.sepa.client.container.init.ContainerModelSubmitter;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;

public class AlgorithmInit extends ContainerModelSubmitter{

	@Override
	public void init() {
		DeclarersSingleton.getInstance().setRoute("algorithms");
        DeclarersSingleton.getInstance()
                .add(new LanguageDetectionController());
	}
}
