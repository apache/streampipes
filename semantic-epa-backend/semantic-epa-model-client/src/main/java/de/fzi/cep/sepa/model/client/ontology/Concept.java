package de.fzi.cep.sepa.model.client.ontology;

import java.util.ArrayList;
import java.util.List;

public class Concept extends OntologyElement {
	
	private List<Statement> statements;
	
	public Concept(String rdfsLabel, String rdfsDescription)
	{
		super(rdfsLabel, rdfsDescription);
		this.statements = new ArrayList<>();
	}
	
	public Concept()
	{
		this.statements = new ArrayList<>();
	}

	public List<Statement> getStatements() {
		return statements;
	}

	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}
	
	
	
	
}
