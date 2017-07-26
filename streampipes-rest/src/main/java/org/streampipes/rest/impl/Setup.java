package org.streampipes.rest.impl;

import java.io.File;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonObject;

import org.streampipes.commons.config.ConfigurationManager;
import org.streampipes.commons.config.WebappConfigurationSettings;
import org.streampipes.manager.setup.Installer;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.messages.Notifications;
import org.streampipes.rest.api.ISetup;
import org.streampipes.rest.notifications.NotificationListener;

@Path("/v2/setup")
public class Setup extends AbstractRestInterface implements ISetup {

	@GET
    @Path("/configured")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response isConfigured()
    {
    	JsonObject obj = new JsonObject();
			if (ConfigurationManager.isConfigured()) 
				{
					obj.addProperty("configured", true);
					return ok(obj.toString());
				}
			else 
			{
				obj.addProperty("configured", false);
				return ok(obj.toString());
			}
    }
    
    @POST
    @Path("/install")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response configure(WebappConfigurationSettings settings)
    {
    	String configFileLocation = ConfigurationManager.getStreamPipesConfigFileLocation();
    	String configFilename = ConfigurationManager.getStreamPipesConfigFilename();

    	List<Message> successMessages = new Installer(settings, new File(configFileLocation + configFilename), new File(configFileLocation)).install();
    	new NotificationListener().contextInitialized(null);
    	return ok(successMessages);
    }
    
    @PUT
    @Path("/configuration")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response updateConfiguration(WebappConfigurationSettings settings)
    {
    	try {
			ConfigurationManager
                    .storeWebappConfigurationToProperties(
                            new File(ConfigurationManager.getStreamPipesConfigFullPath()),
                            new File(ConfigurationManager.getStreamPipesConfigFileLocation()),
                            settings);
			return ok(Notifications.success("Configuration updated"));
    	} catch (Exception e) {
    		e.printStackTrace();
    		return ok(Notifications.error("Error"));
    	}
    }
    
    @GET
    @Path("/configuration")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getConfiguration()
    {
    	return ok(ConfigurationManager.getWebappConfigurationFromProperties());
    }

}
