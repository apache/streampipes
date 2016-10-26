package de.fzi.cep.sepa.model.impl.staticproperty;


import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;
import de.fzi.cep.sepa.model.util.Cloner;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:RemoteOneOfStaticProperty")
@Entity
public class RemoteOneOfStaticProperty extends StaticProperty {

	private static final long serialVersionUID = 3483290363677184344L;

	@OneToMany(fetch = FetchType.EAGER,
			cascade = {CascadeType.ALL})
	@RdfProperty("sepa:hasOption")
	List<Option> options;

	@RdfProperty("sepa:remoteUrl")
	private String remoteUrl;

	@RdfProperty("sepa:valueFieldName")
	private String valueFieldName;

	@RdfProperty("sepa:labelFieldName")
	private String labelFieldName;

	@RdfProperty("sepa:descriptionFieldName")
	private String descriptionFieldName;

	public RemoteOneOfStaticProperty() {
		super(StaticPropertyType.OneOfStaticProperty);
		this.options = new ArrayList<Option>();
	}

	public RemoteOneOfStaticProperty(String internalName, String label, String description, String remoteUrl, String valueFieldName, String labelFieldName, String descriptionFieldName, boolean valueRequired) {
		this.remoteUrl = remoteUrl;
		this.valueFieldName = valueFieldName;
		this.labelFieldName = labelFieldName;
		this.descriptionFieldName = descriptionFieldName;
		this.valueRequired = valueRequired;
		this.options = new ArrayList<Option>();
	}


	public RemoteOneOfStaticProperty(String internalName, String label, String description, String remoteUrl, String valueFieldName, String labelFieldName, String descriptionFieldName) {
		this.remoteUrl = remoteUrl;
		this.valueFieldName = valueFieldName;
		this.labelFieldName = labelFieldName;
		this.descriptionFieldName = descriptionFieldName;
		this.options = new ArrayList<Option>();
	}

	public RemoteOneOfStaticProperty(RemoteOneOfStaticProperty other) {
		super(other);
		this.remoteUrl = other.getRemoteUrl();
		this.valueFieldName = other.getValueFieldName();
		this.labelFieldName = other.getLabelFieldName();
		this.descriptionFieldName = other.getDescriptionFieldName();
		this.options = new Cloner().options(other.getOptions());
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public void setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
	}

	public String getValueFieldName() {
		return valueFieldName;
	}

	public void setValueFieldName(String valueFieldName) {
		this.valueFieldName = valueFieldName;
	}

	public String getLabelFieldName() {
		return labelFieldName;
	}

	public void setLabelFieldName(String labelFieldName) {
		this.labelFieldName = labelFieldName;
	}

	public String getDescriptionFieldName() {
		return descriptionFieldName;
	}

	public void setDescriptionFieldName(String descriptionFieldName) {
		this.descriptionFieldName = descriptionFieldName;
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}
}
