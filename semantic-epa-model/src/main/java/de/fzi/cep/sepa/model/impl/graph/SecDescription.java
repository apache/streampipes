package de.fzi.cep.sepa.model.impl.graph;

import java.util.ArrayList;
import javax.persistence.Entity;


import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.ConsumableSEPAElement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:SemanticEventConsumer")
@Entity
public class SecDescription extends ConsumableSEPAElement{
	
	private static final long serialVersionUID = -6553066396392585731L;
	
	public SecDescription(String uri, String name, String description, String iconUrl)
	{
		super(uri, name, description, iconUrl);
		this.eventStreams = new ArrayList<>();
	}
	
	public SecDescription()
	{
		super();
	}

}
