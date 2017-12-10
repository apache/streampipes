package org.streampipes.model.output;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.util.Cloner;
import org.streampipes.vocabulary.StreamPipes;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.URI_PROPERTY_MAPPING)
@Entity
public class UriPropertyMapping extends UnnamedStreamPipesEntity {

	@RdfProperty(StreamPipes.REPLACE_FROM)
	private URI replaceFrom;
	
	@RdfProperty(StreamPipes.REPLACE_TO)
	private URI replaceTo;
	
	@RdfProperty(StreamPipes.REPLACE_WITH)
	private EventProperty replaceWith;
	
	@RdfProperty(StreamPipes.RENAMING_ALLOWED)
	private boolean renamingAllowed;
	
	@RdfProperty(StreamPipes.TYPE_CAST_ALLOWED)
	private boolean typeCastAllowed;
	
	@RdfProperty(StreamPipes.DOMAIN_PROPERTY_CAST_ALLOWED)
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
