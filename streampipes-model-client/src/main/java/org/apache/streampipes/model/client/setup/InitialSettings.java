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

package org.apache.streampipes.model.client.setup;

public class InitialSettings {

	private String adminUser;
	private String adminPassword;
	private String initialServiceAccountName;
	private String initialServiceAccountSecret;
	private Boolean installPipelineElements;

	public InitialSettings() {

	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public Boolean getInstallPipelineElements() {
		return installPipelineElements;
	}

	public void setInstallPipelineElements(Boolean installPipelineElements) {
		this.installPipelineElements = installPipelineElements;
	}

	public String getAdminUser() {
		return adminUser;
	}

	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}

	public String getInitialServiceAccountName() {
		return initialServiceAccountName;
	}

	public void setInitialServiceAccountName(String initialServiceAccountName) {
		this.initialServiceAccountName = initialServiceAccountName;
	}

	public String getInitialServiceAccountSecret() {
		return initialServiceAccountSecret;
	}

	public void setInitialServiceAccountSecret(String initialServiceAccountSecret) {
		this.initialServiceAccountSecret = initialServiceAccountSecret;
	}
}
