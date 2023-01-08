/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.rest.impl.admin;


import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.config.backend.MessagingSettings;
import org.apache.streampipes.rest.core.base.impl.AbstractRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.svcdiscovery.api.ISpKvManagement;
import org.apache.streampipes.svcdiscovery.api.model.ConfigItem;
import org.apache.streampipes.svcdiscovery.api.model.PeConfig;
import org.apache.streampipes.svcdiscovery.consul.ConsulSpConfig;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Path("/v2/consul")
@Component
public class ConsulConfig extends AbstractRestResource {

  private static final Logger LOG = LoggerFactory.getLogger(ConsulConfig.class);

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response getAllServiceConfigs() {
    LOG.info("Request for all service configs");
    Map<String, String> peServices = getServiceDiscovery().getExtensionsServiceGroups();

    List<PeConfig> peConfigs = new LinkedList<>();

    for (Map.Entry<String, String> entry : peServices.entrySet()) {
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

    return ok(peConfigs);
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response saveServiceConfig(PeConfig peConfig) {

    ISpKvManagement keyValueStore = getKeyValueStore();
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
      JsonObject jsonObj = new Gson().toJsonTree(configItem).getAsJsonObject();
      jsonObj.entrySet().removeIf(e -> e.getKey().equals("key"));
      keyValueStore.updateConfig(configItem.getKey(), jsonObj.toString(),
          configItem.isPassword());
    }
    return Response.status(Response.Status.OK).build();
  }

  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response deleteService(String serviceName) {
    LOG.info("Request to delete a service config");
    getServiceDiscovery().deregisterService(serviceName);
    return Response.status(Response.Status.OK).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @Path("/messaging")
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response getMessagingSettings() {
    return ok(BackendConfig.INSTANCE.getMessagingSettings());
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @Path("messaging")
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response updateMessagingSettings(MessagingSettings messagingSettings) {
    BackendConfig.INSTANCE.setMessagingSettings(messagingSettings);
    return ok();
  }

  public List<ConfigItem> getConfigForService(String serviceId) {
    Map<String, String> keyValues = getKeyValueStore().getKeyValue(ConsulSpConfig.SERVICE_ROUTE_PREFIX + serviceId);

    List<ConfigItem> configItems = new LinkedList<>();

    for (Map.Entry<String, String> entry : keyValues.entrySet()) {
      ConfigItem configItem = new Gson().fromJson(entry.getValue(), ConfigItem.class);
      configItem.setKey(entry.getKey());

      configItems.add(configItem);
    }
    return configItems;
  }


}
