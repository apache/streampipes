package de.fzi.cep.sepa.client.html.page;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.html.model.Description;


public abstract class WelcomePageGenerator<T> {

	protected List<Description> descriptions;
	protected List<T> declarers;
	protected String baseUri;
	
	public WelcomePageGenerator(String baseUri, List<T> declarers) {
		this.declarers = declarers;
		this.baseUri = baseUri;
		this.descriptions = new ArrayList<>();
	}
	
	public abstract List<Description> buildUris();
}
