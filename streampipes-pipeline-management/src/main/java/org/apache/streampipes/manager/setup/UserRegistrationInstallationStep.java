/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.manager.setup;

import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.model.client.user.User;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.util.PasswordUtil;

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
			StorageDispatcher.INSTANCE.getNoSqlStore().getUserStorageAPI().storeUser(new User(adminEmail,
							encryptedPassword, roles));
			return Arrays.asList(Notifications.success(getTitle()));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return Arrays.asList(Notifications.error("Could not encrypt password"));
		}

	}

	@Override
	public String getTitle() {
		return "Creating admin user...";
	}

}
