package org.streampipes.container.html.page;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.container.html.model.Description;


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

    public List<Description> getDescriptions() {
        return descriptions;
    }
}
