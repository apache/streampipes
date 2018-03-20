/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.model.client.messages;

import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;

import com.google.gson.annotations.SerializedName;

public class ProaSenseNotificationMessage {

	private @SerializedName("_id") String id;
    private @SerializedName("_rev") String rev;
    
	private String notificationId;
	private String targetedUser;
	private String title;
	private long timestamp;
	private String description;
	private boolean read;
	
	public ProaSenseNotificationMessage(String title, long creationDate, String description, String targetedUser) {
		this.notificationId = RandomStringUtils.randomAlphanumeric(10);
		this.id = UUID.randomUUID().toString();
		this.targetedUser = targetedUser;
		this.title = title;
		this.timestamp = creationDate;
		this.description = description;
		this.read = false;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getTargetedUser() {
		return targetedUser;
	}

	public void setTargetedUser(String targetedUser) {
		this.targetedUser = targetedUser;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}
		
}
