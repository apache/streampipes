package org.streampipes.manager.setup;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.model.client.user.Role;
import org.streampipes.model.client.user.User;
import org.streampipes.storage.controller.StorageManager;

public class UserRegistrationInstallationStep implements InstallationStep {

	private String adminEmail;
	private String adminUsername;
	private String adminPassword;
	private Set<Role> roles;
	
	public UserRegistrationInstallationStep(String adminEmail,
			String adminUsername, String adminPassword) {
		this.adminEmail = adminEmail;
		this.adminUsername = adminUsername;
		this.adminPassword = adminPassword;
		roles = new HashSet<>();
		roles.add(Role.SYSTEM_ADMINISTRATOR);
		roles.add(Role.USER_DEMO);
	}

	@Override
	public List<Message> install() {
		StorageManager.INSTANCE.getUserStorageAPI().storeUser(new User(adminUsername, adminEmail, adminPassword, roles));
		
		return Arrays.asList(Notifications.success("Creating admin user..."));
	}

}
