package de.fzi.cep.sepa.model.impl;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.UnnamedSEPAElement;
import de.fzi.cep.sepa.model.impl.quality.EventPropertyQuality;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQuality;
import de.fzi.cep.sepa.model.impl.quality.RequiresEventPropertyQuality;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:EventProperty")
@MappedSuperclass
@Entity
public abstract class EventProperty extends UnnamedSEPAElement {

	protected static final String prefix = "urn:fzi.de:sepa:";
	
	String propertyId;
	
	@RdfProperty("rdfs:label")
	protected String label;
	
	@RdfProperty("rdfs:description")
	protected String description;
	
	@RdfProperty("sepa:hasRuntimeName")
	protected String runtimeName;
	
	@RdfProperty("sepa:required")
	protected boolean required;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:domainProperty")
	protected List<URI> subClassOf;
	

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty("sepa:hasEventPropertyQuality")
	List<EventPropertyQuality> eventPropertyQualities;

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty("sepa:requiresEventPropertyQuality")
	List<RequiresEventPropertyQuality> requiresEventPropertyQualities;


	public EventProperty()
	{
		super(prefix + UUID.randomUUID().toString());
		this.propertyId = UUID.randomUUID().toString();
	}
	
	public EventProperty(List<URI> subClassOf)
	{
		this();
		this.subClassOf = subClassOf;
	}
		
	public EventProperty(String propertyName, List<URI> subClassOf) {
		this();
		this.runtimeName = propertyName;		
		this.subClassOf = subClassOf;
	}
	
	public EventProperty(String propertyName, List<URI> subClassOf, List<EventPropertyQuality> eventPropertyQualities) {
		this();
		this.runtimeName = propertyName;		
		this.subClassOf = subClassOf;
		this.eventPropertyQualities = eventPropertyQualities;
	}

	public EventProperty(String propertyName) {
		this();
		this.runtimeName = propertyName;		
	}
	
	
	public List<RequiresEventPropertyQuality> getRequiresEventPropertyQualities() {
		return requiresEventPropertyQualities;
	}

	public void setRequiresEventPropertyQualities(
			List<RequiresEventPropertyQuality> requiresEventPropertyQualities) {
		this.requiresEventPropertyQualities = requiresEventPropertyQualities;
	}

	public String getRuntimeName() {
		return runtimeName;
	}
	public void setRuntimeName(String propertyName) {
		this.runtimeName = propertyName;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}	
	
	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
	
	public List<URI> getSubClassOf() {
		return subClassOf;
	}

	public void setSubClassOf(List<URI> subClassOf) {
		this.subClassOf = subClassOf;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String humanReadableTitle) {
		this.label = humanReadableTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String humanReadableDescription) {
		this.description = humanReadableDescription;
	}

	public List<EventPropertyQuality> getEventPropertyQualities() {
		return eventPropertyQualities;
	}

	public void setEventPropertyQualities(
			List<EventPropertyQuality> eventPropertyQualities) {
		this.eventPropertyQualities = eventPropertyQualities;
	}

	public static String getPrefix() {
		return prefix;
	}


	public abstract Map<String, Object> getRuntimeFormat();
	
	public abstract Map<String, Object> getUntypedRuntimeFormat();
	
	public abstract List<String> getFullPropertyName(String prefix);
	
}
