package de.fzi.cep.sepa.model;


import com.clarkparsia.empire.annotation.RdfId;
import com.clarkparsia.empire.annotation.RdfProperty;
import de.fzi.cep.sepa.model.impl.ApplicationLink;
import de.fzi.cep.sepa.model.util.Cloner;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

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

	@OneToMany(fetch = FetchType.EAGER,
			cascade = {CascadeType.ALL})
	@RdfProperty("sepa:applicationLink")
	protected List<ApplicationLink> applicationLinks;

    protected String elementId;

	protected String DOM;
    protected List<String> connectedTo;


    public NamedSEPAElement()
	{
		super();
		this.applicationLinks = new ArrayList<>();
	}
	
	public NamedSEPAElement(String uri)
	{
		super();
		this.uri = uri;
		this.applicationLinks = new ArrayList<>();
	}
	
	public NamedSEPAElement(String uri, String name, String description, String iconUrl)
	{
		this(uri, name, description);
		this.iconUrl = iconUrl;
		this.applicationLinks = new ArrayList<>();
	}
	
	public NamedSEPAElement(String uri, String name, String description)
	{
		super();
		this.uri = uri;
		this.name = name;
		this.description = description;
		this.elementId = uri;
		this.applicationLinks = new ArrayList<>();
	}

	public NamedSEPAElement(NamedSEPAElement other) {
		super(other);
		this.description = other.getDescription();
		this.name = other.getName();
		this.iconUrl = other.getIconUrl();
		this.uri = other.getUri();
		this.DOM = other.getDOM();
        this.connectedTo = other.getConnectedTo();
		this.elementId = other.getElementId();
		if (other.getApplicationLinks() != null) {
			this.applicationLinks = new Cloner().al(other.getApplicationLinks());
		}
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

	public void setElementId(String elementId) {
		this.uri = elementId;
	}

	public void setDOM(String DOM) {
		this.DOM = DOM;
	}

	public String getDOM() {
		return DOM;
	}

    public List<String> getConnectedTo() {
        return connectedTo;
    }

    public void setConnectedTo(List<String> connectedTo) {
        this.connectedTo = connectedTo;
    }

	public List<ApplicationLink> getApplicationLinks() {
		return applicationLinks;
	}

	public void setApplicationLinks(List<ApplicationLink> applicationLinks) {
		this.applicationLinks = applicationLinks;
	}
}
