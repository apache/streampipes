package org.streampipes.model.base;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.grounding.EventGrounding;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

public abstract class ConsumableStreamPipesEntity extends NamedStreamPipesEntity {

	private static final long serialVersionUID = -6617391345752016449L;

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.REQUIRES_STREAM)
	protected List<SpDataStream> spDataStreams;

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.HAS_STATIC_PROPERTY)
	protected List<StaticProperty> staticProperties;
	
	@OneToOne(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.SUPPORTED_GROUNDING)
  private EventGrounding supportedGrounding;
	
	public ConsumableStreamPipesEntity()
	{
		super();
		this.spDataStreams = new ArrayList<>();
		this.staticProperties = new ArrayList<>();
	}
	
	public ConsumableStreamPipesEntity(String uri, String name, String description, String iconUrl)
	{
		super(uri, name, description, iconUrl);
		this.spDataStreams = new ArrayList<>();
		this.staticProperties = new ArrayList<>();
	}
	
	public ConsumableStreamPipesEntity(ConsumableStreamPipesEntity other) {
		super(other);
		if (other.getSpDataStreams() != null) this.spDataStreams = new Cloner().streams(other.getSpDataStreams());
		this.staticProperties = new Cloner().staticProperties(other.getStaticProperties());
		if (other.getSupportedGrounding() != null) this.supportedGrounding = new EventGrounding(other.getSupportedGrounding());	
	}

	public List<SpDataStream> getSpDataStreams() {
		return spDataStreams;
	}

	public void setSpDataStreams(List<SpDataStream> spDataStreams) {
		this.spDataStreams = spDataStreams;
	}

	public List<StaticProperty> getStaticProperties() {
		return staticProperties;
	}

	public void setStaticProperties(List<StaticProperty> staticProperties) {
		this.staticProperties = staticProperties;
	}
	
	public boolean addEventStream(SpDataStream spDataStream)
	{
		return spDataStreams.add(spDataStream);
	}

	public EventGrounding getSupportedGrounding() {
		return supportedGrounding;
	}

	public void setSupportedGrounding(EventGrounding supportedGrounding) {
		this.supportedGrounding = supportedGrounding;
	}
	
}
