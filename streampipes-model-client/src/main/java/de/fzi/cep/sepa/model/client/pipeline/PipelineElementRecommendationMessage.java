package de.fzi.cep.sepa.model.client.pipeline;

import java.util.ArrayList;
import java.util.List;

public class PipelineElementRecommendationMessage {

	private List<PipelineElementRecommendation> possibleElements;
	private List<PipelineElementRecommendation> recommendedElements;
	
	boolean success;
	
	public PipelineElementRecommendationMessage()
	{
		this.possibleElements = new ArrayList<>();
		this.recommendedElements = new ArrayList<>();
		this.success = true;
	}

	public List<PipelineElementRecommendation> getPossibleElements() {
		return possibleElements;
	}

	public void addPossibleElement(PipelineElementRecommendation recommendation)
	{
		possibleElements.add(recommendation);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<PipelineElementRecommendation> getRecommendedElements() {
		return recommendedElements;
	}

	public void setRecommendedElements(
			List<PipelineElementRecommendation> recommendedElements) {
		this.recommendedElements = recommendedElements;
	}

	public void setPossibleElements(List<PipelineElementRecommendation> possibleElements) {
		this.possibleElements = possibleElements;
	}
	
	
}
