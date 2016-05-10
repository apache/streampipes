package de.fzi.cep.sepa.model.client.ontology;

public class Statement {

	private String predicate;
	private String object;
	
	public Statement(String predicate, String object) {
		super();
		this.predicate = predicate;
		this.object = object;
	}
	
	public Statement() {
		
	}
	
	public String getPredicate() {
		return predicate;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	
	
}
