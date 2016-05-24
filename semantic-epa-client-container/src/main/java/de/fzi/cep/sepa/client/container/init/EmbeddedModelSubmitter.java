package de.fzi.cep.sepa.client.container.init;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public abstract class EmbeddedModelSubmitter implements ServletContextListener {

    //TODO make dynamic and load the value from config
    public static String getBaseUri() {
        return "http://localhost:8081/stream-story/api/v1.1.1/";
    }

    public void contextInitialized(ServletContextEvent arg0)
    {
        init();
    }


    public void contextDestroyed(ServletContextEvent arg0)
    {
    }

    /**
     * This Method needs to be implemented to instantiate an client container
     * Use the DeclarersSingleton to register the declarers
     */
    public abstract void init();

}
