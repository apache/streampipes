package de.fzi.cep.sepa.messages;

import java.util.ArrayList;
import java.util.List;

public class RecommendationMessage {

	private List<ElementRecommendation> possibleElements;
	private List<ElementRecommendation> recommendedElements;
	
	boolean success;
	
	public RecommendationMessage()
	{
		this.possibleElements = new ArrayList<>();
		this.recommendedElements = new ArrayList<>();
		this.success = true;
	}

	public List<ElementRecommendation> getPossibleElements() {
		return possibleElements;
	}

	public void addPossibleElement(ElementRecommendation recommendation)
	{
		possibleElements.add(recommendation);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<ElementRecommendation> getRecommendedElements() {
		return recommendedElements;
	}

	public void setRecommendedElements(
			List<ElementRecommendation> recommendedElements) {
		this.recommendedElements = recommendedElements;
	}

	public void setPossibleElements(List<ElementRecommendation> possibleElements) {
		this.possibleElements = possibleElements;
	}
	
	
}
