package org.streampipes.model.impl.staticproperty;

import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import java.net.URI;

import javax.persistence.Entity;

@RdfsClass("sepa:MatchingStaticProperty")
@Entity
public class MatchingStaticProperty extends StaticProperty{

	private static final long serialVersionUID = -6033310221105761979L;

	@RdfProperty("sepa:matchLeft")
	URI matchLeft;
	
	@RdfProperty("sepa:matchRight")
	URI matchRight;

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
	
	public void accept(StaticPropertyVisitor visitor) {
		visitor.visit(this);
	}	
	
}
