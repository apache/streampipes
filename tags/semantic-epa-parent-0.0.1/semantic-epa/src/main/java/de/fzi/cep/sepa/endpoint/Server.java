package de.fzi.cep.sepa.endpoint;

import java.util.List;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

public enum Server {
	
	INSTANCE;
	
	private final Router router = new Router();
	
	public boolean createEmbedded(List<RestletConfig> restletConfigs) {
		router.setDefaultMatchingMode(Router.MODE_BEST_MATCH);
		
		restletConfigs.forEach(r -> router.attach(r.getUri(), r.getRestlet()));

	    return true;
	}
	
	public boolean create(int port, List<RestletConfig> restletConfigs)
	{
	    createEmbedded(restletConfigs);
	    Component component = new Component();
	    component.getServers().add(Protocol.HTTP, port);
	    component.getDefaultHost().setRoutes(router.getRoutes());
	  
	    try {
			component.start();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	    
	    return true;
	   
	}
		
	public Router getRouter()
	{
		return router;
	}
		
}
