package org.streampipes.model.base;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.monitoring.ElementStatusInfoSettings;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.util.Cloner;
import org.streampipes.model.util.Logger;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

public abstract class InvocableStreamPipesEntity extends NamedStreamPipesEntity {

	private static final long serialVersionUID = 2727573914765473470L;

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.RECEIVES_STREAM)
	protected List<SpDataStream> inputStreams;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.HAS_STATIC_PROPERTY)
	protected List<StaticProperty> staticProperties;
	
	@RdfProperty(StreamPipes.BELONGS_TO)
	private String belongsTo;

	@OneToOne(fetch = FetchType.EAGER,
					cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty(StreamPipes.STATUS_INFO_SETTINGS)
	private ElementStatusInfoSettings statusInfoSettings;

	private EventGrounding supportedGrounding;
	
	@RdfProperty(StreamPipes.CORRESPONDING_PIPELINE)
	private String correspondingPipeline;

	private List<SpDataStream> streamRequirements;

	private boolean configured;

	public InvocableStreamPipesEntity() {
		super();
	}
	
	public InvocableStreamPipesEntity(InvocableStreamPipesEntity other)
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
	
	public InvocableStreamPipesEntity(String uri, String name, String description, String iconUrl) {
		super(uri, name, description, iconUrl);
		this.configured = false;
	}

	public boolean addStaticProperty(StaticProperty staticProperty)
	{
		return staticProperties.add(staticProperty);
	}

	public List<SpDataStream> getInputStreams() {
		return inputStreams;
	}

	public void setInputStreams(List<SpDataStream> inputStreams) {
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

	public List<SpDataStream> getStreamRequirements() {
		return streamRequirements;
	}

	public void setStreamRequirements(List<SpDataStream> streamRequirements) {
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

	public Logger getLogger(Class clazz) {
		return Logger.getLogger(clazz, getCorrespondingPipeline(), getUri());
	}
}
