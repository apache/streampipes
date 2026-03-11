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

package org.apache.streampipes.extensions.connectors.camel.kamelet.assets;

import org.apache.streampipes.commons.constants.GlobalStreamPipesConstants;
import org.apache.streampipes.extensions.api.assets.AssetResolver;
import org.apache.streampipes.extensions.connectors.camel.kamelet.model.KameletTemplate;
import org.apache.streampipes.extensions.connectors.camel.kamelet.provider.KameletTemplateProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class KameletSinkAssetResolver implements AssetResolver {

  private final String appId;
  private final String templateName;
  private final String assetRoot;
  private final KameletTemplateProvider templateProvider;
  private final KameletDocumentationGenerator documentationGenerator;

  public KameletSinkAssetResolver(String appId,
                                  String templateName,
                                  String assetRoot,
                                  KameletTemplateProvider templateProvider) {
    this.appId = appId;
    this.templateName = templateName;
    this.assetRoot = assetRoot;
    this.templateProvider = templateProvider;
    this.documentationGenerator = new KameletDocumentationGenerator();
  }

  @Override
  public byte[] getAsset(ClassLoader classLoader,
                         String assetName) throws IOException {
    KameletTemplate template = templateProvider.requireTemplate(templateName);

    if (GlobalStreamPipesConstants.STD_DOCUMENTATION_NAME.equals(assetName)) {
      return documentationGenerator.generateDocumentation(template);
    }

    if (GlobalStreamPipesConstants.STD_ICON_NAME.equals(assetName)) {
      InputStream iconStream = getResourceStream(classLoader, makeAssetPath(template.name(), assetName));
      if (iconStream == null) {
        iconStream = getResourceStream(classLoader, makeAssetPath(template.name(), "icon.svg"));
      }
      if (iconStream != null) {
        return iconStream.readAllBytes();
      }

      if (template.embeddedIcon() != null && template.embeddedIcon().length > 0) {
        return template.embeddedIcon();
      }

      throw new IOException(String.format("Could not read icon for template %s", template.name()));
    }

    InputStream stream = getResourceStream(classLoader, makeAssetPath(template.name(), assetName));
    if (stream == null) {
      throw new IOException(String.format("Could not read asset %s for template %s", assetName, template.name()));
    }

    return stream.readAllBytes();
  }

  @Override
  public Properties getLocale(ClassLoader classLoader,
                              String localeName) throws IOException {
    KameletTemplate template = templateProvider.requireTemplate(templateName);
    Properties properties = new Properties();

    InputStream localeStream = getResourceStream(classLoader, makeAssetPath(template.name(), localeName));
    if (localeStream != null) {
      properties.load(new InputStreamReader(localeStream, StandardCharsets.UTF_8));
    }

    properties.put(appId + ".title", template.displayName());
    properties.put(appId + ".description", template.description());

    return properties;
  }

  private String makeAssetPath(String templateName,
                               String assetName) {
    return assetRoot + "/" + templateName + "/" + assetName;
  }

  private InputStream getResourceStream(ClassLoader classLoader,
                                        String path) {
    return classLoader.getResourceAsStream(path);
  }
}
