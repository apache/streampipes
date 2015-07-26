package de.fzi.cep.sepa.rest;

import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.commons.config.Configuration;


import javax.ws.rs.*;

/**
 * REST interfacer for configuration
 *
 * Created by robin on 26.07.15.
 */
@Path("/config")
public class ConfigurationRest extends AbstractRestInterface {

    @GET
    public String getConfig(@PathParam("key") String key) {
        return Configuration.getInstance().getConfig().getProperty(key).toString();
    };

    @POST
    public String setConfig(@QueryParam("key") String key, @QueryParam("value") String value) {
        Configuration.getInstance().getConfig().addProperty(key, value);
        return constructSuccessMessage(new Notification(NotificationType.ADDED_CONFIGURATION.title(), NotificationType.ADDED_CONFIGURATION.description()));
    }

}
