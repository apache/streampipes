package de.fzi.cep.sepa.messages;

import java.util.ArrayList;
import java.util.List;

public class RecommendationMessage {

	private List<ElementRecommendation> recommendedElements;
	boolean success;
	
	public RecommendationMessage()
	{
		this.recommendedElements = new ArrayList<>();
		this.success = true;
	}

	public List<ElementRecommendation> getRecommendedElements() {
		return recommendedElements;
	}

	public void addRecommendation(ElementRecommendation recommendation)
	{
		recommendedElements.add(recommendation);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setRecommendedElements(
			List<ElementRecommendation> recommendedElements) {
		this.recommendedElements = recommendedElements;
	}
	
}
