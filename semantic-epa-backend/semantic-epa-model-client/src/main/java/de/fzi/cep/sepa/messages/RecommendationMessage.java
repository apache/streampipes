package de.fzi.cep.sepa.messages;

import java.util.ArrayList;
import java.util.List;

public class RecommendationMessage {

	private List<ElementRecommendation> recommendedElements;
	
	public RecommendationMessage()
	{
		this.recommendedElements = new ArrayList<>();
	}

	public List<ElementRecommendation> getRecommendedElements() {
		return recommendedElements;
	}

	public void addRecommendation(ElementRecommendation recommendation)
	{
		recommendedElements.add(recommendation);
	}
	
	
}
