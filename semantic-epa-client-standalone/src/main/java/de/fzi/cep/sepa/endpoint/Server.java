package de.fzi.cep.sepa.endpoint;
 
import java.util.List;

import org.restlet.Component;
import org.restlet.data.Protocol;
 
public enum Server {
        
        INSTANCE;
        
        private Component component;
 
        public boolean create(int port, List<RestletConfig> restletConfigs)
        {
                this.component = new Component();
                component.getServers().add(Protocol.HTTP, port);
                
                for(RestletConfig config : restletConfigs)
                {
                        component.getDefaultHost().attach(config.getUri(), config.getRestlet());
                }
                
                try {
                        component.start();
                } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                }
                return true;
        }
        
        public Component getComponent()
        {
                return component;
        }
                
}