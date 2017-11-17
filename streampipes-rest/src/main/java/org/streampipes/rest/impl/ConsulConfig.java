package org.streampipes.rest.impl;

import com.google.gson.Gson;
import org.streampipes.config.model.ConfigItem;
import org.streampipes.config.model.PeConfig;
import org.streampipes.container.util.ConsulUtil;
import org.streampipes.rest.annotation.GsonWithIds;
import org.streampipes.rest.api.IConsulConfig;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.streampipes.container.util.ConsulUtil.updateConfig;

@Path("/v2/consul")
public class ConsulConfig implements IConsulConfig {

    private static final String SERVICE_ROUTE_PREFIX = "sp/v1/";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response getAllServiceConfigs() {
        Map<String, String> peServices = ConsulUtil.getPEServices();

        List peConfigs = new LinkedList<PeConfig>();

        for(Map.Entry<String, String> entry: peServices.entrySet()) {
            String serviceName = entry.getKey();
            String serviceStatus = entry.getValue();

            Map meta = new HashMap<String, String>();
            meta.put("status", serviceStatus);
            List<ConfigItem> configItems = getConfigForService(serviceName);

            PeConfig peConfig = new PeConfig();
            peConfig.setName(serviceName);
            peConfig.setMeta(meta);
            peConfig.setConfigs(configItems);

            String d = new Gson().toJson(peConfig);
            peConfigs.add(peConfig);
        }

        String json = new Gson().toJson(peConfigs);
        return Response.ok(json).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response saveServiceConfig(PeConfig peConfig) {
        for (ConfigItem configItem: peConfig.getConfigs()) {
            updateConfig(configItem.getKey(),
                            configItem.getValue(),
                            configItem.getDescription());
        }
        return Response.status(Response.Status.OK).build();
    }

    private List<ConfigItem> getConfigForService(String serviceId) {
        Map<String, String> keyValues = ConsulUtil.getKeyValue(SERVICE_ROUTE_PREFIX + serviceId);

        List<ConfigItem> configItems = new LinkedList<>();

        for(Map.Entry<String, String> entry : keyValues.entrySet()) {
            String key = entry.getKey();
            if(!key.endsWith("_description")) {
                ConfigItem configItem = new ConfigItem();
                configItem.setKey(entry.getKey());
                configItem.setValue(entry.getValue());
                String descriptionKey = key + "_description";
                if(keyValues.containsKey(descriptionKey)) {
                    configItem.setDescription(keyValues.get(descriptionKey));
                }
                configItems.add(configItem);
            }
        }
        return configItems;
    }
}
