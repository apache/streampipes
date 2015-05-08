package de.fzi.cep.sepa.manager.recommender;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;

import com.rits.cloning.Cloner;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.exceptions.NoSepaInPipelineException;
import de.fzi.cep.sepa.commons.exceptions.NoSuitableSepasAvailableException;
import de.fzi.cep.sepa.commons.exceptions.NoValidConnectionException;
import de.fzi.cep.sepa.manager.matching.PipelineValidationHandler;
import de.fzi.cep.sepa.manager.util.ClientModelUtils;
import de.fzi.cep.sepa.messages.ElementRecommendation;
import de.fzi.cep.sepa.messages.RecommendationMessage;
import de.fzi.cep.sepa.model.client.ConsumableSEPAElement;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.util.ClientModelTransformer;

public class ElementRecommender {

	private Pipeline pipeline;
	private RecommendationMessage recommendationMessage;
	private Cloner cloner;
	
	public ElementRecommender(Pipeline partialPipeline)
	{
		this.pipeline = partialPipeline;
		this.recommendationMessage = new RecommendationMessage();
		this.cloner = new Cloner();
	}
	
	public RecommendationMessage findRecommendedElements() throws NoSuitableSepasAvailableException
	{
		String connectedTo;
		try {
			ConsumableSEPAElement sepaElement = getRootNode();
			connectedTo = sepaElement.getDOM();
		} catch (NoSepaInPipelineException e) {
			connectedTo = pipeline.getStreams().get(0).getDOM();
		}
		
		List<SEPA> sepas = getAllSepas();
		for(SEPA sepa : sepas)
		{
			try {
				Pipeline tempPipeline = cloner.deepClone(pipeline);
				tempPipeline.getSepas().add(generateSepaClient(sepa, connectedTo));
				new PipelineValidationHandler(tempPipeline, true).validateConnection();
				addRecommendation(sepa);
				tempPipeline.setSepas(new ArrayList<>());
			} catch (NoValidConnectionException e) {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (recommendationMessage.getRecommendedElements().size() == 0) throw new NoSuitableSepasAvailableException();
		else return recommendationMessage;
	}
	
	private SEPAClient generateSepaClient(SEPA sepa, String connectedTo)
	{
		SEPAClient sepaClient = ClientModelTransformer.toSEPAClientModel(sepa);
		sepaClient.setConnectedTo(Utils.createList(connectedTo));
		sepaClient.setDOM(RandomStringUtils.randomAlphanumeric(5));
		return sepaClient;
	}
	
	private void addRecommendation(SEPA sepa) {
		recommendationMessage.addRecommendation(new ElementRecommendation(sepa.getRdfId().toString(), sepa.getName(), sepa.getDescription()));
	}

	private List<SEPA> getAllSepas()
	{
		return StorageManager.INSTANCE.getStorageAPI().getAllSEPAs();
	}
	
	private ConsumableSEPAElement getRootNode() throws NoSepaInPipelineException
	{
		return ClientModelUtils.getRootNode(pipeline);
	}
}
