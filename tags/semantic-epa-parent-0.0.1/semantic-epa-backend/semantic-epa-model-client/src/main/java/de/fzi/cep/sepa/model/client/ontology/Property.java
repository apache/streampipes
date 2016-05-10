package de.fzi.cep.sepa.model.client.ontology;

public class Property extends OntologyElement {
	
	private Range range;
	
	private boolean rangeDefined;
	private boolean labelDefined;
	
	public Property(ElementHeader elementHeader, String rdfsLabel, String rdfsDescription, Range range)
	{
		super(elementHeader, rdfsLabel, rdfsDescription);
		this.range = range;
		this.labelDefined = true;
		this.rangeDefined = true;
	}

	public Property() {
		// TODO Auto-generated constructor stub
	}

	public Property(ElementHeader header) {
		super(header);
		this.rangeDefined = false;
		this.labelDefined = false;
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public boolean isRangeDefined() {
		return rangeDefined;
	}

	public void setRangeDefined(boolean rangeDefined) {
		this.rangeDefined = rangeDefined;
	}

	public boolean isLabelDefined() {
		return labelDefined;
	}

	public void setLabelDefined(boolean labelDefined) {
		this.labelDefined = labelDefined;
	}
	
	@Override 
	public int hashCode() {
		   return this.getElementHeader().getId().hashCode();
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (! (other instanceof Property)) return false;
		else return this.getElementHeader().getId().equals(((Property)other).getElementHeader().getId());
	}
	
	
}
