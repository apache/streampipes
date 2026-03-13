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
package org.apache.streampipes.manager.extensions;

import org.apache.streampipes.commons.exceptions.SepaParseException;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTarget;
import org.apache.streampipes.manager.api.extensions.param.ExtensionDescriptionParameters;
import org.apache.streampipes.manager.verification.extractor.TypeExtractor;
import org.apache.streampipes.model.extensions.ExtensionItemInstallationRequest;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.message.Message;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import java.io.IOException;

public class ExtensionItemInstaller {

  private final ExtensionServiceRequestManager requestManager;
  private final SpServiceRegistration service;

  public ExtensionItemInstaller(SpServiceRegistration service,
                                ExtensionServiceRequestManager requestManager) {
    this.requestManager = requestManager;
    this.service = service;
  }

  public Message installExtension(ExtensionItemInstallationRequest req,
                                  String principalSid) throws IOException, SepaParseException {
    var requestTarget = getDescriptionRequestTarget(req);
    var description = fetchDescription(requestTarget);
    return new TypeExtractor(description, requestManager).getTypeVerifier().verifyAndAdd(principalSid, req.publicElement());
  }

  public Message updateExtension(ExtensionItemInstallationRequest req) throws IOException, SepaParseException {
    var requestTarget = getDescriptionRequestTarget(req);
    var description = fetchDescription(requestTarget);
    return new TypeExtractor(description, requestManager).getTypeVerifier().verifyAndUpdate();
  }

  private ExtensionServiceRequestTarget getDescriptionRequestTarget(ExtensionItemInstallationRequest req) {
    return new ExtensionServiceRequestTarget(
        service.getServiceUrl(),
        service.getSvcId(),
        new ExtensionDescriptionParameters(
            SpServiceUrlProvider.valueOf(req.serviceTagPrefix().name()),
            req.appId()
        )
    );
  }

  private String fetchDescription(ExtensionServiceRequestTarget requestTarget) throws IOException {
    return requestManager.requestExtensionDescription(requestTarget).responseBody();
  }
}
