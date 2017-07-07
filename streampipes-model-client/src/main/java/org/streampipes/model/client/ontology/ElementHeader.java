package org.streampipes.model.client.ontology;

public class ElementHeader {

	private String id;
	private String title;
	
	private String prefix;
	private String namespace;
	
		
	public ElementHeader(String id, String title, String prefix,
			String namespace) {
		super();
		this.id = id;
		this.title = title;
		this.prefix = prefix;
		this.namespace = namespace;
	}
	
	public ElementHeader(String propertyId, String title) {
		this.id = propertyId;
		this.title = title;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	
}
