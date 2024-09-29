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

import org.apache.streampipes.manager.assets.AssetManager;
import org.apache.streampipes.manager.extensions.AvailableExtensionsProvider;
import org.apache.streampipes.manager.extensions.ExtensionsResourceUrlProvider;
import org.apache.streampipes.model.extensions.ExtensionItemDescription;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.fluent.Request;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/extension-items")
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class ExtensionsServiceEndpointResource extends AbstractAuthGuardedRestResource {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ExtensionItemDescription>> getExtensionItems() {
    var allExtensions = new AvailableExtensionsProvider(getNoSqlStorage()).getExtensionItemDescriptions();
    return ok(allExtensions);
  }

  @PostMapping(path = "/icon", produces = "image/png")
  public ResponseEntity<byte[]> getExtensionItemIcon(@RequestBody ExtensionItemDescription endpointItem) {
    try {
      byte[] imageBytes = getIconImage(endpointItem);
      return ok(imageBytes);
    } catch (IOException e) {
      throw new SpMessageException(HttpStatus.BAD_REQUEST, e);
    }
  }

  private byte[] getIconImage(ExtensionItemDescription extensionItemDescription) throws IOException {
    if (extensionItemDescription.isInstalled()) {
      return AssetManager.getAssetIcon(extensionItemDescription.getAppId());
    } else {
      var iconUrl = new ExtensionsResourceUrlProvider(SpServiceDiscovery.getServiceDiscovery())
              .getIconUrl(extensionItemDescription);
      return Request.Get(iconUrl).execute().returnContent().asBytes();
    }
  }
}
