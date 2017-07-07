package org.streampipes.model.impl.output;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import org.streampipes.model.UnnamedSEPAElement;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.util.Cloner;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:UriPropertyMapping")
@Entity
public class UriPropertyMapping extends UnnamedSEPAElement {

	@RdfProperty("sepa:replaceFrom")
	private URI replaceFrom;
	
	@RdfProperty("sepa:replaceTo")
	private URI replaceTo;
	
	@RdfProperty("sepa:replaceWith")
	private EventProperty replaceWith;
	
	@RdfProperty("sepa:renamingAllowed")
	private boolean renamingAllowed;
	
	@RdfProperty("sepa:typeCastAllowed")
	private boolean typeCastAllowed;
	
	@RdfProperty("sepa:domainPropertyCastAllowed")
	private boolean domainPropertyCastAllowed;

	private List<EventProperty> replaceWithOptions;
	
	public UriPropertyMapping() {
		super();
		this.replaceWithOptions = new ArrayList<>();
	}
	
	public UriPropertyMapping(UriPropertyMapping other) {
		super();
		this.replaceFrom = other.getReplaceFrom();
		if (this.replaceWith != null) this.replaceWith = new Cloner().property(other.getReplaceWith());
		this.replaceTo = other.getReplaceTo();
		this.renamingAllowed = other.isRenamingAllowed();
		this.typeCastAllowed = other.isTypeCastAllowed();
		this.domainPropertyCastAllowed = other.isDomainPropertyCastAllowed();
		this.replaceWithOptions = new Cloner().properties(other.getReplaceWithOptions());
	}

	public URI getReplaceFrom() {
		return replaceFrom;
	}

	public EventProperty getReplaceWith() {
		return replaceWith;
	}
	
	public void setReplaceFrom(URI replaceFrom) {
		this.replaceFrom = replaceFrom;
	}

	public void setReplaceWith(EventProperty replaceWith) {
		this.replaceWith = replaceWith;
	}

	public boolean isRenamingAllowed() {
		return renamingAllowed;
	}

	public void setRenamingAllowed(boolean renamingAllowed) {
		this.renamingAllowed = renamingAllowed;
	}

	public boolean isTypeCastAllowed() {
		return typeCastAllowed;
	}

	public void setTypeCastAllowed(boolean typeCastAllowed) {
		this.typeCastAllowed = typeCastAllowed;
	}

	public boolean isDomainPropertyCastAllowed() {
		return domainPropertyCastAllowed;
	}

	public void setDomainPropertyCastAllowed(boolean domainPropertyCastAllowed) {
		this.domainPropertyCastAllowed = domainPropertyCastAllowed;
	}

	public URI getReplaceTo() {
		return replaceTo;
	}

	public void setReplaceTo(URI replaceTo) {
		this.replaceTo = replaceTo;
	}

	public List<EventProperty> getReplaceWithOptions() {
		return replaceWithOptions;
	}

	public void setReplaceWithOptions(List<EventProperty> replaceWithOptions) {
		this.replaceWithOptions = replaceWithOptions;
	}
}
