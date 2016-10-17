package de.fzi.cep.sepa.rest.notifications;

import de.fzi.cep.sepa.commons.config.ConfigurationManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class NotificationListener implements ServletContextListener {

	private static final String iccsKafkaTopic = "eu.proasense.internal.pandda.mhwirth.recommendation";
	private static final String iccsKafkaHellaTopic = "eu.proasense.internal.pandda.hella.recommendation";
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
			new Thread(new StreamPipesNotificationSubscriber(internalNotificationTopic)).start();
			new Thread(new ProaSenseNotificationSubscriber(iccsKafkaTopic)).start();
			new Thread(new ProaSenseNotificationSubscriber(iccsKafkaHellaTopic)).start();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
