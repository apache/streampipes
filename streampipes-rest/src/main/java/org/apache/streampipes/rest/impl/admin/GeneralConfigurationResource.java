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
import org.apache.streampipes.config.backend.model.GeneralConfig;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.StringWriter;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

@Path("/v2/admin/general-config")
@Component
public class GeneralConfigurationResource extends AbstractAuthGuardedRestResource {

  @GET
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public GeneralConfig getGeneralConfiguration() {
    return BackendConfig.INSTANCE.getGeneralConfig();
  }

  @PUT
  @JacksonSerialized
  @Consumes(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response updateGeneralConfiguration(GeneralConfig config) {
    config.setConfigured(true);
    BackendConfig.INSTANCE.updateGeneralConfig(config);

    return ok();
  }

  @GET
  @Path("keys")
  @Produces("multipart/mixed")
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public Response generateKeyPair() throws Exception {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    KeyPair keyPair = kpg.genKeyPair();

    String publicKeyPem = exportKeyAsPem(keyPair.getPublic(), "PUBLIC");
    String privateKeyPem = exportKeyAsPem(keyPair.getPrivate(), "PRIVATE");

    MultiPart multiPartEntity = new MultiPart()
        .bodyPart(new BodyPart().entity(publicKeyPem))
        .bodyPart(new BodyPart().entity(privateKeyPem));

    return Response.ok(multiPartEntity).build();
  }

  private String exportKeyAsPem(Key key, String keyType) {
    StringWriter sw = new StringWriter();

    sw.write("-----BEGIN " + keyType + " KEY-----\n");
    sw.write(Base64.getEncoder().encodeToString(key.getEncoded()));
    sw.write("\n-----END " + keyType + " KEY-----\n");

    return sw.toString();
  }
}
