package de.fzi.cep.sepa.rest.notifications;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.fzi.cep.sepa.commons.config.ConfigurationManager;

public class NotificationListener implements ServletContextListener {

	private static final String iccsKafkaTopic = "proasense.recommendationevents";
	private static final String internalNotificationTopic = "de.fzi.cep.sepa.notifications";
	

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		if (ConfigurationManager.isConfigured())
		{
			try {
			new ProaSenseNotificationSubscriber().subscribe(internalNotificationTopic);
			new ProaSenseNotificationSubscriber().subscribe(iccsKafkaTopic);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
