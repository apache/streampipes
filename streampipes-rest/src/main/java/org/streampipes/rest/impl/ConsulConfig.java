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

import static org.streampipes.container.util.ConsulUtil.updateConfig;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.config.backend.BackendConfig;
import org.streampipes.config.backend.MessagingSettings;
import org.streampipes.config.consul.ConsulSpConfig;
import org.streampipes.config.model.ConfigItem;
import org.streampipes.config.model.PeConfig;
import org.streampipes.container.util.ConsulUtil;
import org.streampipes.rest.api.IConsulConfig;
import org.streampipes.rest.shared.annotation.GsonWithIds;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

    for (Map.Entry<String, String> entry : peServices.entrySet()) {
      String serviceName = "";
      String serviceStatus = entry.getValue();
      String mainKey = ConsulSpConfig.SERVICE_ROUTE_PREFIX + entry.getKey();

      Map<String, String> meta = new HashMap<>();
      meta.put("status", serviceStatus);
      List<ConfigItem> configItems = getConfigForService(entry.getKey());

      PeConfig peConfig = new PeConfig();

      for (ConfigItem configItem : configItems) {
        if (configItem.getKey().endsWith("SP_SERVICE_NAME")) {
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
    for (ConfigItem configItem : peConfig.getConfigs()) {
      String value = configItem.getValue();
      switch (configItem.getValueType()) {
        case "xs:boolean":
          if (!("true".equals(value) || "false".equals(value))) {
            LOG.error(value + " is not from the type: xs:boolean");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(value + " is not from the type: xs:boolean").build();
          }
          break;
        case "xs:integer":
          try {
            Integer.valueOf(value);
          } catch (java.lang.NumberFormatException e) {
            LOG.error(value + " is not from the type: xs:integer");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(value + " is not from the type: xs:integer").build();
          }
          break;
        case "xs:double":
          try {
            Double.valueOf(value);
          } catch (java.lang.NumberFormatException e) {
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

    for (ConfigItem configItem : peConfig.getConfigs()) {
      updateConfig(configItem.getKey(), new Gson().toJson(configItem),
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

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Path("/messaging")
  @Override
  public Response getMessagingSettings() {
    return ok(BackendConfig.INSTANCE.getMessagingSettings());
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Path("messaging")
  @Override
  public Response updateMessagingSettings(MessagingSettings messagingSettings) {
    BackendConfig.INSTANCE.setMessagingSettings(messagingSettings);
    return ok();
  }

  public List<ConfigItem> getConfigForService(String serviceId) {
    Map<String, String> keyValues = ConsulUtil.getKeyValue(ConsulSpConfig.SERVICE_ROUTE_PREFIX + serviceId);

    List<ConfigItem> configItems = new LinkedList<>();

    for (Map.Entry<String, String> entry : keyValues.entrySet()) {
      ConfigItem configItem = new Gson().fromJson(entry.getValue(), ConfigItem.class);
      configItem.setKey(entry.getKey());

      configItems.add(configItem);
    }
    return configItems;
  }
}
