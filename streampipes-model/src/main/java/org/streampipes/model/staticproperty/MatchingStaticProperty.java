package org.streampipes.model.staticproperty;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import java.net.URI;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.MATCHING_STATIC_PROPERTY)
@Entity
public class MatchingStaticProperty extends StaticProperty{

	private static final long serialVersionUID = -6033310221105761979L;

	@RdfProperty(StreamPipes.MATCH_LEFT)
	private URI matchLeft;
	
	@RdfProperty(StreamPipes.MATCH_RIGHT)
	private URI matchRight;

	public MatchingStaticProperty()
	{
		super(StaticPropertyType.MatchingStaticProperty);
	}
	
	public MatchingStaticProperty(MatchingStaticProperty other) {
		super(other);
		this.matchLeft = other.getMatchLeft();
		this.matchRight = other.getMatchRight();
	}
	
	public MatchingStaticProperty(String internalName, String label, String description)
	{
		super(StaticPropertyType.MatchingStaticProperty, internalName, label, description);
	}
	
	public MatchingStaticProperty(String internalName, String label, String description, URI matchLeft, URI matchRight) {
		super(StaticPropertyType.MatchingStaticProperty, internalName, label, description);
		this.matchLeft = matchLeft;
		this.matchRight = matchRight;
	}

	public URI getMatchLeft() {
		return matchLeft;
	}

	public void setMatchLeft(URI matchLeft) {
		this.matchLeft = matchLeft;
	}

	public URI getMatchRight() {
		return matchRight;
	}

	public void setMatchRight(URI matchRight) {
		this.matchRight = matchRight;
	}
}
