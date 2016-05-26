package de.fzi.cep.sepa.algorithm.main;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.algorithm.languagedetection.LanguageDetectionController;
import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.client.declarer.SemanticEventProcessingAgentDeclarer;

public class Init {

	public static void main(String[] args) throws Exception
	{
		List<SemanticEventProcessingAgentDeclarer> declarers = new ArrayList<>();
//		declarers.add(new LanguageDetectionController());

//		ModelSubmitter.submitAgent(declarers, ClientConfiguration.INSTANCE.getAlgorithmPort());
	}
}
