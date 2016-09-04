package de.fzi.cep.sepa.rest.impl;

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

import de.fzi.cep.sepa.commons.config.ConfigurationManager;
import de.fzi.cep.sepa.commons.config.WebappConfigurationSettings;
import de.fzi.cep.sepa.manager.setup.Installer;
import de.fzi.cep.sepa.messages.Message;
import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.rest.api.ISetup;
import de.fzi.cep.sepa.rest.notifications.NotificationListener;

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
					obj.addProperty("appConfig", ConfigurationManager.getWebappConfigurationFromProperties().getAppConfig());
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
