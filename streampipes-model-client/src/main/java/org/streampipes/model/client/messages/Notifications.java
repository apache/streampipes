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
	
	public static ErrorMessage error(NotificationType type)
	{
		return new ErrorMessage(new Notification(type.name(), type.description()));
	}
	
	public static ErrorMessage error(String message)
	{
		return new ErrorMessage(new Notification(message, ""));
	}
	
	public static SuccessMessage success(String message)
	{
		return new SuccessMessage(new Notification(message, ""));
	}
	
	public static ErrorMessage error(NotificationType type, String info)
	{
		return new ErrorMessage(new Notification(type.name(), type.description(), info));
	}
}
