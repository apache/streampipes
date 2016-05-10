package de.fzi.cep.sepa.model.impl.graph;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.ConsumableSEPAElement;
import de.fzi.cep.sepa.model.util.Cloner;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:SemanticEventConsumer")
@Entity
public class SecDescription extends ConsumableSEPAElement{
	
	private static final long serialVersionUID = -6553066396392585731L;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:ecType")
	protected List<String> ecTypes;
	
	public SecDescription(String uri, String name, String description, String iconUrl)
	{
		super(uri, name, description, iconUrl);
		this.eventStreams = new ArrayList<>();
		this.ecTypes = new ArrayList<>();
	}
	
	public SecDescription(SecDescription other)
	{
		super(other);
		this.ecTypes = new Cloner().ecTypes(other.getEcTypes());
	}
	
	public SecDescription(String uri, String name, String description)
	{
		this(uri, name, description, "");
		this.ecTypes = new ArrayList<>();
	}
	
	public SecDescription()
	{
		super();
		this.ecTypes = new ArrayList<>();
	}

	public List<String> getEcTypes() {
		return ecTypes;
	}

	public void setEcTypes(List<String> ecTypes) {
		this.ecTypes = ecTypes;
	}

	
}
