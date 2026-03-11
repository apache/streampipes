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
package org.apache.streampipes.manager.setup;

import org.apache.streampipes.commons.exceptions.SepaParseException;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.extensions.ExtensionItemInstaller;
import org.apache.streampipes.manager.extensions.ExtensionsResourceUrlProvider;
import org.apache.streampipes.model.extensions.ExtensionItemDescription;
import org.apache.streampipes.model.extensions.ExtensionItemInstallationRequest;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;

import java.io.IOException;

public class PipelineElementInstallationStep extends InstallationStep {

  private final ExtensionItemDescription extensionItem;
  private final String principalSid;
  private final ExtensionServiceRequestManager extensionServiceRequestManager;


  public PipelineElementInstallationStep(ExtensionItemDescription extensionItem,
                                         String principalSid,
                                         ExtensionServiceRequestManager extensionServiceRequestManager) {
    this.extensionItem = extensionItem;
    this.principalSid = principalSid;
    this.extensionServiceRequestManager = extensionServiceRequestManager;
  }

  @Override
  public void install() {
    var installationReq = ExtensionItemInstallationRequest.fromDescription(extensionItem, true);
    var resourceUrlProvider = new ExtensionsResourceUrlProvider(SpServiceDiscovery.getServiceDiscovery());
    try {
      new ExtensionItemInstaller(resourceUrlProvider, extensionServiceRequestManager)
          .installExtension(installationReq, principalSid);
      logSuccess(getTitle());
    } catch (SepaParseException | IOException e) {
      logFailure(getTitle());
    }
  }

  @Override
  public String getTitle() {
    return "Installing extension " + extensionItem.getName();
  }
}
