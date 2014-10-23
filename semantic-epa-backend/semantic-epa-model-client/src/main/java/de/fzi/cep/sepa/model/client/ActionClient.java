package de.fzi.cep.sepa.model.client;

import javax.persistence.Entity;

@Entity
public class ActionClient extends SEPAElement {

	public ActionClient(String name, String description) {
		super(name, description);
		
	}

}
