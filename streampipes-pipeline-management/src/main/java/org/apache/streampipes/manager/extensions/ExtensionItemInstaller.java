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
import org.apache.streampipes.manager.api.extensions.IExtensionsResourceUrlProvider;
import org.apache.streampipes.manager.execution.ExtensionServiceExecutions;
import org.apache.streampipes.manager.verification.extractor.TypeExtractor;
import org.apache.streampipes.model.extensions.ExtensionItemInstallationRequest;
import org.apache.streampipes.model.message.Message;

import java.io.IOException;

public class ExtensionItemInstaller {

  private final IExtensionsResourceUrlProvider urlProvider;

  public ExtensionItemInstaller(IExtensionsResourceUrlProvider urlProvider) {
    this.urlProvider = urlProvider;
  }

  public Message installExtension(ExtensionItemInstallationRequest req,
                                  String principalSid) throws IOException, SepaParseException {
    var descriptionUrl = getDescriptionUrl(req);
    var description = fetchDescription(descriptionUrl);
    return new TypeExtractor(description).getTypeVerifier().verifyAndAdd(principalSid, req.publicElement());
  }

  public Message updateExtension(ExtensionItemInstallationRequest req) throws IOException, SepaParseException {
    var descriptionUrl = getDescriptionUrl(req);
    var description = fetchDescription(descriptionUrl);
    return new TypeExtractor(description).getTypeVerifier().verifyAndUpdate();
  }

  private String getDescriptionUrl(ExtensionItemInstallationRequest req) {
    return urlProvider.getDescriptionUrl(req);
  }

  private String fetchDescription(String descriptionUrl) throws IOException {
    return ExtensionServiceExecutions.extServiceGetRequest(descriptionUrl).execute().returnContent().asString();
  }
}
