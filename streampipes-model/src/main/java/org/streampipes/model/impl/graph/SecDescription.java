package org.streampipes.model.impl.graph;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.ConsumableSEPAElement;
import org.streampipes.model.util.Cloner;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@RdfsClass("sepa:SemanticEventConsumer")
@Entity
public class SecDescription extends ConsumableSEPAElement {
	
	private static final long serialVersionUID = -6553066396392585731L;
	
	@OneToMany(fetch = FetchType.EAGER,
			   cascade = {CascadeType.ALL})
	@RdfProperty("sepa:ecType")
	private List<String> category;
	
	public SecDescription(String uri, String name, String description, String iconUrl)
	{
		super(uri, name, description, iconUrl);
		this.eventStreams = new ArrayList<>();
		this.category = new ArrayList<>();
	}
	
	public SecDescription(SecDescription other)
	{
		super(other);
		this.category = new Cloner().ecTypes(other.getCategory());
	}
	
	public SecDescription(String uri, String name, String description)
	{
		this(uri, name, description, "");
		this.category = new ArrayList<>();
	}
	
	public SecDescription()
	{
		super();
		this.category = new ArrayList<>();
	}

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}

	
}
