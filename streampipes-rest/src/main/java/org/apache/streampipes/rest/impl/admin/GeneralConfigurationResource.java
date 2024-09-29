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

import org.apache.streampipes.model.configuration.GeneralConfig;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;

import java.io.StringWriter;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/admin/general-config")
public class GeneralConfigurationResource extends AbstractAuthGuardedRestResource {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public GeneralConfig getGeneralConfiguration() {
    return getSpCoreConfigurationStorage().get().getGeneralConfig();
  }

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<Void> updateGeneralConfiguration(@RequestBody GeneralConfig config) {
    config.setConfigured(true);
    var storage = getSpCoreConfigurationStorage();
    var cfg = storage.get();
    cfg.setGeneralConfig(config);
    storage.updateElement(cfg);

    return ok();
  }

  @GetMapping(path = "keys", produces = MediaType.MULTIPART_MIXED_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<?> generateKeyPair() throws Exception {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    KeyPair keyPair = kpg.genKeyPair();

    String publicKeyPem = exportKeyAsPem(keyPair.getPublic(), "PUBLIC");
    String privateKeyPem = exportKeyAsPem(keyPair.getPrivate(), "PRIVATE");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_MIXED);

    String boundary = "boundarysp";
    headers.set(HttpHeaders.CONTENT_TYPE, "multipart/mixed;boundary=" + boundary);

    // Construct the response body with multiple parts
    String responseBody = "--" + boundary + "\r\n" + "Content-Type: text/plain\r\n\r\n" + publicKeyPem + "\r\n" + "--"
            + boundary + "\r\n" + "Content-Type: text/plain\r\n\r\n" + privateKeyPem + "\r\n" + "--" + boundary + "--";

    return ResponseEntity.ok().headers(headers).body(responseBody.getBytes());
  }

  private String exportKeyAsPem(Key key, String keyType) {
    StringWriter sw = new StringWriter();

    sw.write("-----BEGIN " + keyType + " KEY-----\n");
    sw.write(Base64.getEncoder().encodeToString(key.getEncoded()));
    sw.write("\n-----END " + keyType + " KEY-----\n");

    return sw.toString();
  }
}
