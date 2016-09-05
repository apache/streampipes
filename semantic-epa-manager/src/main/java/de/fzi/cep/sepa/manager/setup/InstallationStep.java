package de.fzi.cep.sepa.manager.setup;

import java.util.List;

import de.fzi.cep.sepa.model.client.messages.Message;

public interface InstallationStep {

	List<Message> install();
}
