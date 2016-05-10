package de.fzi.cep.sepa.html;

import java.util.ArrayList;
import java.util.List;


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
