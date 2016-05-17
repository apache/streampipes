package de.fzi.cep.sepa.model.client.ontology;

public abstract class Range {

	private String title;
	private String description;
	
	private RangeType rangeType;
	 
	public Range(RangeType rangeType, String title, String description) {
		super();
		this.title = title;
		this.rangeType = rangeType;
		this.description = description;
	}
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public RangeType getRangeType() {
		return rangeType;
	}

	public void setRangeType(RangeType rangeType) {
		this.rangeType = rangeType;
	}
	
}
