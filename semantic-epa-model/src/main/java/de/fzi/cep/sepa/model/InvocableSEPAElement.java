package de.fzi.cep.sepa.model;

import com.clarkparsia.empire.annotation.RdfProperty;
import de.fzi.cep.sepa.model.impl.ElementStatusInfoSettings;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.util.Cloner;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

public abstract class InvocableSEPAElement extends NamedSEPAElement {

	private static final long serialVersionUID = 2727573914765473470L;

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

	@OneToOne(fetch = FetchType.EAGER,
					cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty("sepa:statusInfoSettings")
	protected ElementStatusInfoSettings statusInfoSettings;

	protected EventGrounding supportedGrounding;
	
	@RdfProperty("sepa:correspondingPipeline")
	protected String correspondingPipeline;

	protected List<EventStream> streamRequirements;

	protected boolean configured;
	
	public InvocableSEPAElement() {
		super();
	}
	
	public InvocableSEPAElement(InvocableSEPAElement other)
	{
		super(other);
		this.belongsTo = other.getBelongsTo();
		this.correspondingPipeline = other.getCorrespondingPipeline();
		this.inputStreams = new Cloner().streams(other.getInputStreams());
		this.configured = other.isConfigured();
        if (other.getStreamRequirements() != null) this.streamRequirements = new Cloner().streams(other.getStreamRequirements());
		if (other.getStaticProperties() != null) this.staticProperties = new Cloner().staticProperties(other.getStaticProperties());
		this.DOM = other.getDOM();
		if (other.getSupportedGrounding() != null) this.supportedGrounding = new EventGrounding(other.getSupportedGrounding());
	}
	
	public InvocableSEPAElement(String uri, String name, String description, String iconUrl) {
		super(uri, name, description, iconUrl);
		this.configured = false;
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

	public String getCorrespondingPipeline() {
		return correspondingPipeline;
	}

	public void setCorrespondingPipeline(String correspondingPipeline) {
		this.correspondingPipeline = correspondingPipeline;
	}

	public List<EventStream> getStreamRequirements() {
		return streamRequirements;
	}

	public void setStreamRequirements(List<EventStream> streamRequirements) {
		this.streamRequirements = streamRequirements;
	}

	public boolean isConfigured() {
		return configured;
	}

	public void setConfigured(boolean configured) {
		this.configured = configured;
	}

	public ElementStatusInfoSettings getStatusInfoSettings() {
		return statusInfoSettings;
	}

	public void setStatusInfoSettings(ElementStatusInfoSettings statusInfoSettings) {
		this.statusInfoSettings = statusInfoSettings;
	}
}
