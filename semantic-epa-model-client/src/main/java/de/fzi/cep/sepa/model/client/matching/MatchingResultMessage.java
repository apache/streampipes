package de.fzi.cep.sepa.model.client.matching;

public class MatchingResultMessage {

	private boolean matchingSuccessful;
	
	private String title;
	private String description;
	
	private String offerSubject;
	private String requirementSubject;
	
	private String reasonText;
	

	public MatchingResultMessage() {
		
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isMatchingSuccessful() {
		return matchingSuccessful;
	}

	public void setMatchingSuccessful(boolean matchingSuccessful) {
		this.matchingSuccessful = matchingSuccessful;
	}

	public String getOfferSubject() {
		return offerSubject;
	}

	public void setOfferSubject(String offerSubject) {
		this.offerSubject = offerSubject;
	}

	public String getRequirementSubject() {
		return requirementSubject;
	}

	public void setRequirementSubject(String requirementSubject) {
		this.requirementSubject = requirementSubject;
	}

	public String getReasonText() {
		return reasonText;
	}

	public void setReasonText(String reasonText) {
		this.reasonText = reasonText;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
