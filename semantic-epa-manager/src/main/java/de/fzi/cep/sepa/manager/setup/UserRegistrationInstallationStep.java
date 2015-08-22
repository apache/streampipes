package de.fzi.cep.sepa.manager.setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.messages.Message;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.model.client.user.Role;
import de.fzi.cep.sepa.model.client.user.User;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class UserRegistrationInstallationStep implements InstallationStep {

	private String adminEmail;
	private String adminUsername;
	private String adminPassword;
	private Set<Role> roles;;
	
	public UserRegistrationInstallationStep(String adminEmail,
			String adminUsername, String adminPassword) {
		this.adminEmail = adminEmail;
		this.adminUsername = adminUsername;
		this.adminPassword = adminPassword;
		roles = new HashSet<>();
		roles.add(Role.ADMINISTRATOR);
		roles.add(Role.USER_DEMO);
	}

	@Override
	public List<Message> install() {
		StorageManager.INSTANCE.getUserStorageAPI().storeUser(new User(adminUsername, adminEmail, adminPassword, roles));
		
		return Arrays.asList(Notifications.success(NotificationType.ADMIN_USER_CREATED));
	}

}
