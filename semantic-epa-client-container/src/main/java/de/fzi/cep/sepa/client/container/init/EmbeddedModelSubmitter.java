package de.fzi.cep.sepa.client.container.init;

import de.fzi.cep.sepa.client.init.ModelSubmitter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public abstract class EmbeddedModelSubmitter extends ModelSubmitter implements ServletContextListener {

    public void contextInitialized(ServletContextEvent arg0)
    {
        init();
    }
}
