package de.fzi.cep.sepa.manager.setup;

import java.util.List;

import de.fzi.cep.sepa.messages.Message;

public interface InstallationStep {

	public List<Message> install();
}
