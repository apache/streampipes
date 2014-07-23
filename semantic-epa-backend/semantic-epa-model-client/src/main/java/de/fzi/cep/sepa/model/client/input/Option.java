package de.fzi.cep.sepa.model.client.input;

import java.util.UUID;


public class Option {

	private UUID uuid;
	private String humanDescription;
	private boolean selected;
	
	public Option(String humanDescription)
	{
		this.uuid = UUID.randomUUID();
		this.humanDescription = humanDescription;
	}

	
	public UUID getUuid() {
		return uuid;
	}

	public String getHumanDescription() {
		return humanDescription;
	}

	public void setHumanDescription(String humanDescription) {
		this.humanDescription = humanDescription;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	
	
}
