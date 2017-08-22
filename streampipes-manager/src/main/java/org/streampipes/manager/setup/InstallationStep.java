package org.streampipes.manager.setup;

import java.util.List;

import org.streampipes.model.client.messages.Message;

public interface InstallationStep {

	List<Message> install();
}
