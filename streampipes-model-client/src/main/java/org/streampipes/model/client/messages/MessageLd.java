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

import org.apache.commons.lang.RandomStringUtils;
import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfId;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Namespaces({StreamPipes.NS_PREFIX, StreamPipes.NS})
@RdfsClass(StreamPipes.MESSAGE)
@Entity
public class MessageLd {

	private static final String prefix = "urn:streampipes.org:spi:";

	@RdfId
	@RdfProperty(StreamPipes.HAS_ELEMENT_NAME)
	private String elementId;

	@RdfProperty(StreamPipes.MESSAGE_SUCCESS)
	private boolean success;

	@RdfProperty(StreamPipes.MESSAGE_ELEMENT_NAME)
	private String elementName;

	@OneToMany(fetch = FetchType.EAGER,
			cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.NOTIFICATIONS)
	private List<NotificationLd> notifications;

	public MessageLd() {
		this.elementId = prefix
				+ this.getClass().getSimpleName().toLowerCase()
				+ ":"
				+ RandomStringUtils.randomAlphabetic(6);
		this.elementName = "";
	}

	public MessageLd(MessageLd other) {
		this();
		this.success = other.isSuccess();
		this.elementName = other.getElementName();
		this.notifications = other.getNotifications();
	}

	public MessageLd(boolean success){
		this();
		this.success = success;
		this.notifications = null;
	}

	public MessageLd(boolean success, List<NotificationLd> notifications) {
		this();
		this.success = success;
		this.notifications = notifications;
	}

	public MessageLd(boolean success, List<NotificationLd> notifications, String elementName) {
		this(success, notifications);
		this.elementName = elementName;
	}


	public MessageLd(boolean success, NotificationLd...notifications) {
		this();
		this.success = success;
		this.notifications = new ArrayList<>();
		this.notifications.addAll(Arrays.asList(notifications));
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<NotificationLd> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<NotificationLd> notifications) {
		this.notifications = notifications;
	}
	
	public boolean addNotification(NotificationLd notification)
	{
		return notifications.add(notification);
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	
	
}
