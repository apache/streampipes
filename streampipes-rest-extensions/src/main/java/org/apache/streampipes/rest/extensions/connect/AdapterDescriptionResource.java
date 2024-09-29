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
package org.apache.streampipes.rest.extensions.connect;

import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.locales.LabelGenerator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.rest.shared.impl.AbstractSharedRestInterface;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/worker/adapters")
public class AdapterDescriptionResource extends AbstractSharedRestInterface {

  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdapterDescription> getAdapterDescription(@PathVariable("id") String id) {
    var adapterDescriptionOpt = DeclarersSingleton.getInstance().getAdapter(id);
    if (adapterDescriptionOpt.isPresent()) {
      try {
        var adapterConfiguration = adapterDescriptionOpt.get().declareConfig();
        var localizedDescription = applyLocales(adapterConfiguration);
        return ok(localizedDescription);
      } catch (IOException e) {
        throw new SpMessageException(HttpStatus.INTERNAL_SERVER_ERROR, e);
      }
    } else {
      throw new SpMessageException(HttpStatus.NOT_FOUND,
              Notifications.error(String.format("Could not find adapter with id %s", id)));
    }
  }

  private AdapterDescription applyLocales(IAdapterConfiguration adapterConfiguration) throws IOException {
    var adapterDescription = adapterConfiguration.getAdapterDescription();
    if (adapterDescription.isIncludesLocales()) {
      return new LabelGenerator<>(adapterDescription, true, adapterConfiguration.getAssetResolver()).generateLabels();
    } else {
      return adapterDescription;
    }
  }
}
