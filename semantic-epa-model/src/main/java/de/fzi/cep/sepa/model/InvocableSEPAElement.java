package de.fzi.cep.sepa.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.clarkparsia.empire.annotation.RdfProperty;

import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.StaticProperty;

public abstract class InvocableSEPAElement extends NamedSEPAElement {

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:receives")
	protected List<EventStream> inputStreams;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasStaticProperty")
	protected List<StaticProperty> staticProperties;
	
	@RdfProperty("sepa:belongsTo")
	protected String belongsTo;
	
	protected String domId;
	
	protected EventGrounding supportedGrounding;
	
	public InvocableSEPAElement() {
		super();
	}
	
	public InvocableSEPAElement(String uri, String name, String description, String iconUrl) {
		super(uri, name, description, iconUrl);
	}

	public String getDomId() {
		return domId;
	}
	
	public boolean addStaticProperty(StaticProperty staticProperty)
	{
		return staticProperties.add(staticProperty);
	}

	public List<EventStream> getInputStreams() {
		return inputStreams;
	}

	public void setInputStreams(List<EventStream> inputStreams) {
		this.inputStreams = inputStreams;
	}

	public List<StaticProperty> getStaticProperties() {
		return staticProperties;
	}

	public void setStaticProperties(List<StaticProperty> staticProperties) {
		this.staticProperties = staticProperties;
	}

	public void setDomId(String domId) {
		this.domId = domId;
	}

	public String getBelongsTo() {
		return belongsTo;
	}

	public void setBelongsTo(String belongsTo) {
		this.belongsTo = belongsTo;
	}

	public EventGrounding getSupportedGrounding() {
		return supportedGrounding;
	}

	public void setSupportedGrounding(EventGrounding supportedGrounding) {
		this.supportedGrounding = supportedGrounding;
	}	
	
}
