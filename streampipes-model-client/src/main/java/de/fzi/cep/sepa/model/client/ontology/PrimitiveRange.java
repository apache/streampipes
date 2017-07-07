package de.fzi.cep.sepa.model.client.ontology;

public class PrimitiveRange extends Range {

	private static final String TITLE = "";
	private static final String DESCRIPTION = "";
	
	private String rdfsDatatype;

	private String value;
	
	public PrimitiveRange(String rdfsDatatype) {
		super(RangeType.PRIMITIVE, TITLE, DESCRIPTION);
		this.rdfsDatatype = rdfsDatatype;
	}

	public String getRdfsDatatype() {
		return rdfsDatatype;
	}

	public void setRdfsDatatype(String rdfsDatatype) {
		this.rdfsDatatype = rdfsDatatype;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
	
	
}
