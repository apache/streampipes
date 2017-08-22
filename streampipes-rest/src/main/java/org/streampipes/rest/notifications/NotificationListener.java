package org.streampipes.rest.notifications;

import org.streampipes.config.backend.BackendConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class NotificationListener implements ServletContextListener {

	private static final String internalNotificationTopic = "de.fzi.cep.sepa.notifications";
	

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		if (BackendConfig.INSTANCE.isConfigured())
		{
			try {
			new Thread(new StreamPipesNotificationSubscriber(internalNotificationTopic)).start();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
