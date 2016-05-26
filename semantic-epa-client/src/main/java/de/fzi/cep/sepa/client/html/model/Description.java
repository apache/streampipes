package de.fzi.cep.sepa.client.html.model;

import java.net.URI;

public class Description {

	String name;
	String description;
	URI uri;
	
	public Description(String name, String description, URI uri)
	{
		this.name = name;
		this.description = description;
		this.uri = uri;
	}
	
	public Description()
	{
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public URI getUri() {
		return uri;
	}
	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Description that = (Description) o;

        if (!name.equals(that.name)) return false;
        if (!description.equals(that.description)) return false;
        return uri.equals(that.uri);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + uri.hashCode();
        return result;
    }
}
