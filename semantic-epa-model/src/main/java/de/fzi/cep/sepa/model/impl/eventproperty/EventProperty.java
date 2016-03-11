package de.fzi.cep.sepa.model.impl.eventproperty;

import java.net.URI;
import java.util.ArrayList;
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
import de.fzi.cep.sepa.model.impl.quality.EventPropertyQualityDefinition;
import de.fzi.cep.sepa.model.impl.quality.EventPropertyQualityRequirement;
import de.fzi.cep.sepa.model.util.Cloner;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:EventProperty")
@MappedSuperclass
@Entity
public abstract class EventProperty extends UnnamedSEPAElement {

	private static final long serialVersionUID = 7079045979946059387L;

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
	protected List<URI> domainProperties;
	

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty("sepa:hasEventPropertyQualityDefinition")
	protected List<EventPropertyQualityDefinition> eventPropertyQualities;

	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@RdfProperty("sepa:hasEventPropertyQualityRequirement")
	protected List<EventPropertyQualityRequirement> requiresEventPropertyQualities;

	
	
	public EventProperty()
	{
		super(prefix + UUID.randomUUID().toString());
		this.propertyId = UUID.randomUUID().toString();
		this.requiresEventPropertyQualities = new ArrayList<>();
		this.eventPropertyQualities = new ArrayList<>();
	}
	
	public EventProperty(EventProperty other)
	{
		super(other);
		this.label = other.getLabel();
		this.description = other.getDescription();
		this.propertyId = other.getPropertyId();
		this.required = other.isRequired();
		this.requiresEventPropertyQualities = new Cloner().reqEpQualitities(other.getRequiresEventPropertyQualities());
		this.runtimeName = other.getRuntimeName();
		this.eventPropertyQualities = new Cloner().provEpQualities(other.getEventPropertyQualities());
		this.domainProperties = other.getDomainProperties();
	}
	
	public EventProperty(List<URI> subClassOf)
	{
		this();
		this.domainProperties = subClassOf;
	}
		
	public EventProperty(String propertyName, List<URI> subClassOf) {
		this();
		this.runtimeName = propertyName;		
		this.domainProperties = subClassOf;
	}
	
	public EventProperty(String propertyName, List<URI> subClassOf, List<EventPropertyQualityDefinition> eventPropertyQualities) {
		this();
		this.runtimeName = propertyName;		
		this.domainProperties = subClassOf;
		this.eventPropertyQualities = eventPropertyQualities;
	}

	public EventProperty(String propertyName) {
		this();
		this.runtimeName = propertyName;		
	}
	
	
	public List<EventPropertyQualityRequirement> getRequiresEventPropertyQualities() {
		return requiresEventPropertyQualities;
	}

	public void setRequiresEventPropertyQualities(
			List<EventPropertyQualityRequirement> requiresEventPropertyQualities) {
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
	
	public List<URI> getDomainProperties() {
		return domainProperties;
	}

	public void setDomainProperties(List<URI> subClassOf) {
		this.domainProperties = subClassOf;
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

	public List<EventPropertyQualityDefinition> getEventPropertyQualities() {
		return eventPropertyQualities;
	}

	public void setEventPropertyQualities(
			List<EventPropertyQualityDefinition> eventPropertyQualities) {
		this.eventPropertyQualities = eventPropertyQualities;
	}

	public static String getPrefix() {
		return prefix;
	}

	public abstract Map<String, Object> getRuntimeFormat();
	
	public abstract Map<String, Object> getUntypedRuntimeFormat();
	
	public abstract List<String> getFullPropertyName(String prefix);
	
}
