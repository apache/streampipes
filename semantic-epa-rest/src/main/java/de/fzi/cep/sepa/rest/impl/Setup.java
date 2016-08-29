package de.fzi.cep.sepa.rest.impl;

import java.io.File;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de.fzi.cep.sepa.commons.config.ConfigurationManager;
import de.fzi.cep.sepa.commons.config.WebappConfigurationSettings;
import de.fzi.cep.sepa.manager.setup.Installer;
import de.fzi.cep.sepa.messages.Message;
import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.rest.api.ISetup;
import de.fzi.cep.sepa.rest.notifications.NotificationListener;
import de.fzi.sepa.model.client.util.Utils;

@Path("/v2/setup")
public class Setup implements ISetup {

	@GET
    @Path("/configured")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public String isConfigured()
    {
    	JsonObject obj = new JsonObject();
			if (ConfigurationManager.isConfigured()) 
				{
					obj.addProperty("configured", true);
					obj.addProperty("appConfig", ConfigurationManager.getWebappConfigurationFromProperties().getAppConfig());
					return obj.toString();
				}
			else 
			{
				obj.addProperty("configured", false);
				return obj.toString();
			}
    }
    
    @POST
    @Path("/install")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public String configure(String json)
    {
    	String configFileLocation = ConfigurationManager.getStreamPipesConfigFileLocation();
    	String configFilename = ConfigurationManager.getStreamPipesConfigFilename();
    	WebappConfigurationSettings settings = (fromJson(json, WebappConfigurationSettings.class));
   
    	List<Message> successMessages = new Installer(settings, new File(configFileLocation + configFilename), new File(configFileLocation)).install();
    	new NotificationListener().contextInitialized(null);
    	return toJson(successMessages);
    }
    
    @PUT
    @Path("/configuration")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public String updateConfiguration(String json)
    {
    	try {
			ConfigurationManager.storeWebappConfigurationToProperties(new File(ConfigurationManager.getStreamPipesConfigFullPath()), new File(ConfigurationManager.getStreamPipesConfigFileLocation()), new Gson().fromJson(json, WebappConfigurationSettings.class));
			return toJson(Notifications.success("Configuration updated"));
    	} catch (Exception e) {
    		e.printStackTrace();
    		return toJson(Notifications.error("Error"));
    	}
    }
    
    @GET
    @Path("/configuration")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public String getConfiguration()
    {
    	return new Gson().toJson(ConfigurationManager.getWebappConfigurationFromProperties());
    }
    
    private <T> T fromJson(String payload, Class<T> clazz)
	{
		return Utils.getGson().fromJson(payload, clazz);
	}
	
	private <T> String toJson(T object)
	{
		return Utils.getGson().toJson(object);
	}
}
