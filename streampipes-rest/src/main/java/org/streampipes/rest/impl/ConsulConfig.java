/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.rest.impl;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.config.consul.ConsulSpConfig;
import org.streampipes.config.model.ConfigItem;
import org.streampipes.config.model.PeConfig;
import org.streampipes.container.util.ConsulUtil;
import org.streampipes.rest.api.IConsulConfig;
import org.streampipes.rest.shared.annotation.GsonWithIds;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.streampipes.container.util.ConsulUtil.updateConfig;

@Path("/v2/consul")
public class ConsulConfig extends AbstractRestInterface implements IConsulConfig {

    private static Logger LOG = LoggerFactory.getLogger(ConsulConfig.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response getAllServiceConfigs() {
        LOG.info("Request for all service configs");
        Map<String, String> peServices = ConsulUtil.getPEServices();

        List<PeConfig> peConfigs = new LinkedList<>();

        for(Map.Entry<String, String> entry: peServices.entrySet()) {
            String serviceName = "";
            String serviceStatus = entry.getValue();
            String mainKey = ConsulSpConfig.SERVICE_ROUTE_PREFIX + entry.getKey();

            Map<String, String> meta = new HashMap<>();
            meta.put("status", serviceStatus);
            List<ConfigItem> configItems = getConfigForService(entry.getKey());

            PeConfig peConfig = new PeConfig();

            for(ConfigItem configItem: configItems) {
                if(configItem.getKey().equals("SP_SERVICE_NAME")) {
                    configItems.remove(configItem);
                    peConfig.setName(configItem.getValue());
                    break;
                }
            }

            peConfig.setMeta(meta);
            peConfig.setMainKey(mainKey);
            peConfig.setConfigs(configItems);

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
         LOG.info("Request to update a service config");
         for (ConfigItem configItem: peConfig.getConfigs()) {
            String value = configItem.getValue();
            switch (configItem.getValueType()) {
                case "xs:boolean":
                    if(!("true".equals(value) || "false".equals(value))) {
                        LOG.error(value + " is not from the type: xs:boolean");
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity(value + " is not from the type: xs:boolean").build();
                    }
                    break;
                case "xs:integer":
                    try { Integer.valueOf(value); }
                    catch (java.lang.NumberFormatException e) {
                        LOG.error(value + " is not from the type: xs:integer");
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity(value + " is not from the type: xs:integer").build();
                    }
                    break;
                case "xs:double":
                    try { Double.valueOf(value); }
                    catch (java.lang.NumberFormatException e) {
                        LOG.error(value + " is not from the type: xs:double");
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity(value + " is not from the type: xs:double").build();
                    }
                    break;
                case "xs:string":
                    break;
                default:
                    LOG.error(configItem.getValueType() + " is not a supported type");
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(configItem.getValueType() + " is not a supported type").build();
            }
        }

        String prefix = peConfig.getMainKey();

        for (ConfigItem configItem: peConfig.getConfigs()) {
            updateConfig(prefix + "/" + configItem.getKey(),
                            configItem.getValue(),
                            configItem.getValueType(),
                            configItem.getDescription(),
                            configItem.isPassword());
        }
        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response deleteService(String serviceName) {
        LOG.info("Request to delete a service config");
        ConsulUtil.deregisterService(serviceName);
        return Response.status(Response.Status.OK).build();
    }

    private List<ConfigItem> getConfigForService(String serviceId) {
        Map<String, String> keyValues = ConsulUtil.getKeyValue(ConsulSpConfig.SERVICE_ROUTE_PREFIX + serviceId);

        List<ConfigItem> configItems = new LinkedList<>();

        for(Map.Entry<String, String> entry : keyValues.entrySet()) {
            String key = entry.getKey();
            if(!key.endsWith("_description") && !key.endsWith("_type") && !key.endsWith("_isPassword")) {
                ConfigItem configItem = new ConfigItem();

                String[] splittedKey = entry.getKey().split("/");
                String shortKey = splittedKey[splittedKey.length - 1];

                configItem.setKey(shortKey);

                String isPasswordKey = key + "_isPassword";
                if(keyValues.containsKey(isPasswordKey)) {
                    if(keyValues.get(isPasswordKey).equals("true")) {
                        configItem.setPassword(true);
                        configItem.setValue("");
                    }
                }
                if(!configItem.isPassword())
                    configItem.setValue(entry.getValue());

                String descriptionKey = key + "_description";
                if(keyValues.containsKey(descriptionKey)) {
                    configItem.setDescription(keyValues.get(descriptionKey));
                }

                String typeKey = key + "_type";
                if(keyValues.containsKey(typeKey)) {
                    configItem.setValueType(keyValues.get(typeKey));
                }

                configItems.add(configItem);
            }
        }
        return configItems;
    }
}
