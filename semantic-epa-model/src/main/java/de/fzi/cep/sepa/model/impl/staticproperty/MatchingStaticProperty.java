package de.fzi.cep.sepa.model.impl.staticproperty;

import java.net.URI;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
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
		super();
	}
	
	public MatchingStaticProperty(String name, String description)
	{
		super(name, description);
	}
	
	public MatchingStaticProperty(String name, String description, URI matchLeft, URI matchRight) {
		super(name, description);
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
