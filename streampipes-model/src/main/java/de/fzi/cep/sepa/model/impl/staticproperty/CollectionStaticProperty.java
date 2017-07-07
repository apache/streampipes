package de.fzi.cep.sepa.model.impl.staticproperty;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.util.Cloner;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:CollectionStaticProperty")
@Entity
public class CollectionStaticProperty extends StaticProperty {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:member")
	private List<StaticProperty> members;
	
	@RdfProperty("sepa:memberType")
	private String memberType;

	public CollectionStaticProperty() {
		super(StaticPropertyType.CollectionStaticProperty);
	}

	public CollectionStaticProperty(String internalName, String label, String description, List<StaticProperty> members, String memberType) {
		super(StaticPropertyType.CollectionStaticProperty, internalName, label, description);
		this.members = members;
		this.memberType = memberType;
	}

	public CollectionStaticProperty(CollectionStaticProperty other) {
		super(other);
		this.members = new Cloner().staticProperties(other.getMembers());
		this.memberType = other.getMemberType();
	}

	public List<StaticProperty> getMembers() {
		return members;
	}

	public void setMembers(List<StaticProperty> members) {
		this.members = members;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}
}
