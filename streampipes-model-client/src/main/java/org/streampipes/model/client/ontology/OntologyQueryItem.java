package org.streampipes.model.client.ontology;

import java.util.List;

public class OntologyQueryItem {

	private String propertyId;
	
	private List<OntologyQueryResponse> queryResponse;
	
	public OntologyQueryItem(String propertyValue, List<OntologyQueryResponse> queryResponse) {
		super();
		this.propertyId = propertyId;
	}
	
	public String getPropertyId() {
		return propertyId;
	}
	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public List<OntologyQueryResponse> getQueryResponse() {
		return queryResponse;
	}

	public void setQueryResponse(List<OntologyQueryResponse> queryResponse) {
		this.queryResponse = queryResponse;
	}
		
}
