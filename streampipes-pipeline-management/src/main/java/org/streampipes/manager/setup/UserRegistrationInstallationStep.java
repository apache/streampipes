package org.streampipes.manager.setup;

import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.model.client.user.Role;
import org.streampipes.model.client.user.User;
import org.streampipes.manager.storage.StorageManager;
import org.streampipes.user.management.util.PasswordUtil;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserRegistrationInstallationStep implements InstallationStep {

	private String adminEmail;
	private String adminPassword;
	private Set<Role> roles;
	
	public UserRegistrationInstallationStep(String adminEmail, String adminPassword) {
		this.adminEmail = adminEmail;
		this.adminPassword = adminPassword;
		roles = new HashSet<>();
		roles.add(Role.SYSTEM_ADMINISTRATOR);
		roles.add(Role.USER_DEMO);
	}

	@Override
	public List<Message> install() {

		try {
			String encryptedPassword = PasswordUtil.encryptPassword(adminPassword);
			StorageManager.INSTANCE.getUserStorageAPI().storeUser(new User(adminEmail,
							encryptedPassword, roles));
			return Arrays.asList(Notifications.success("Creating admin user..."));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return Arrays.asList(Notifications.error("Could not encrypt password"));
		}

	}

}
