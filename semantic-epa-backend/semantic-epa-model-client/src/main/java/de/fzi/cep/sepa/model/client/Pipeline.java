package de.fzi.cep.sepa.model.client;

import java.util.List;

public class Pipeline {

	List<SEPAClient> sepas;
	List<StreamClient> streams;
	
	ActionClient action;

	public List<SEPAClient> getSepas() {
		return sepas;
	}

	public void setSepas(List<SEPAClient> sepas) {
		this.sepas = sepas;
	}

	public List<StreamClient> getStreams() {
		return streams;
	}

	public void setStreams(List<StreamClient> streams) {
		this.streams = streams;
	}

	public ActionClient getAction() {
		return action;
	}

	public void setAction(ActionClient action) {
		this.action = action;
	}
	
	
}
