package de.fzi.cep.sepa.model;


import com.clarkparsia.empire.annotation.RdfId;
import com.clarkparsia.empire.annotation.RdfProperty;

/**
 * named SEPA elements, can be accessed via the URI provided in @RdfId
 *
 */
public abstract class NamedSEPAElement extends AbstractSEPAElement{

	private static final long serialVersionUID = -98951691820519795L;

	@RdfProperty("sepa:hasName")
	protected String name;
	
	@RdfProperty("rdfs:description")
	protected String description;
	
	@RdfProperty("sepa:hasIconUrl")
	protected String iconUrl;
	
	@RdfProperty("sepa:hasURI")
	@RdfId
	protected String uri;
	
	public NamedSEPAElement()
	{
		super();
	}
	
	public NamedSEPAElement(String uri)
	{
		super();
		this.uri = uri;
	}
	
	public NamedSEPAElement(String uri, String name, String description, String iconUrl)
	{
		this(uri, name, description);
		this.iconUrl = iconUrl;
	}
	
	public NamedSEPAElement(String uri, String name, String description)
	{
		super();
		this.uri = uri;
		this.name = name;
		this.description = description;
	}

	public NamedSEPAElement(NamedSEPAElement other) {
		super(other);
		this.description = other.getDescription();
		this.name = other.getName();
		this.iconUrl = other.getIconUrl();
		this.uri = other.getUri();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}	
	
	public String getElementId()
	{
		return uri;
	}
}
