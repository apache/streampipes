package de.fzi.cep.sepa.model.client;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class ActionClient extends ConsumableSEPAElement {

	@OneToMany(cascade=CascadeType.ALL)
	private List<String> domains;
	
	
	public ActionClient(String name, String description) {
		super(name, description);
		
	}

	public List<String> getDomains() {
		return domains;
	}

	public void setDomains(List<String> domains) {
		this.domains = domains;
	}

	
}
