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

public class Notifications {

	public static Notification create(NotificationType type)
	{
		return new Notification(type.name(), type.description());
	}
	
	public static Notification create(NotificationType type, String info)
	{
		return new Notification(type.name(), type.description(), info);
	}
	
	public static SuccessMessage success(NotificationType type)
	{
		return new SuccessMessage(new Notification(type.name(), type.description()));
	}
	
	public static SuccessMessage success(NotificationType type, String info)
	{
		return new SuccessMessage(new Notification(type.name(), type.description(), info));
	}

	public static SuccessMessage success(String message)
	{
		return new SuccessMessage(new Notification(message, ""));
	}
	
	public static ErrorMessage error(NotificationType type)
	{
		return new ErrorMessage(new Notification(type.name(), type.description()));
	}
	
	public static ErrorMessage error(String message)
	{
		return new ErrorMessage(new Notification(message, ""));
	}

	public static ErrorMessage error(NotificationType type, String info)
	{
		return new ErrorMessage(new Notification(type.name(), type.description(), info));
	}

	//JsonLD
	public static NotificationLd createLd(NotificationType type, String info)
	{
		return new NotificationLd(type.name(), type.description(), info);
	}

	public static SuccessMessageLd successLd(NotificationType type)
	{
		return new SuccessMessageLd(new NotificationLd(type.name(), type.description()));
	}

	public static SuccessMessageLd successLd(NotificationType type, String info)
	{
		return new SuccessMessageLd(new NotificationLd(type.name(), type.description(), info));
	}

	public static SuccessMessageLd successLd(String message)
	{
		return new SuccessMessageLd(new NotificationLd(message, ""));
	}

	public static ErrorMessageLd errorLd(NotificationType type)
	{
		return new ErrorMessageLd(new NotificationLd(type.name(), type.description()));
	}

	public static ErrorMessageLd errorLd(String message)
	{
		return new ErrorMessageLd(new NotificationLd(message, ""));
	}

	public static ErrorMessageLd errorLd(NotificationType type, String info)
	{
		return new ErrorMessageLd(new NotificationLd(type.name(), type.description(), info));
	}
}
