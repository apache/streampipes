package de.fzi.cep.sepa.client.container.init;

import de.fzi.cep.sepa.desc.declarer.Declarer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

public abstract class EmbeddedModelSubmitter implements ServletContextListener {


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
