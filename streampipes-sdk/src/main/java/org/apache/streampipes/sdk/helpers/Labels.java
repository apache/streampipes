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

package org.apache.streampipes.sdk.helpers;

import org.apache.streampipes.commons.resources.Resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class Labels {

  private static final Logger LOG = LoggerFactory.getLogger(Labels.class);

  /**
   * @deprecated Externalize labels by using
   * {@link org.apache.streampipes.sdk.builder.AbstractProcessingElementBuilder#withLocales(Locales...)}
   * to ease future support for multiple languages.
   *
   * Creates a new label with internalId, label and description. Fully-configured labels are required by static
   * properties and are mandatory for event properties.
   *
   * @param internalId  The internal identifier of the element, e.g., "latitude-field-mapping"
   * @param label       A human-readable title
   * @param description A human-readable brief summary of the element.
   * @return
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public static Label from(String internalId, String label, String description) {
    return new Label(internalId, label, description);
  }

  /**
   * @deprecated Externalize labels by using
   * {@link org.apache.streampipes.sdk.builder.AbstractProcessingElementBuilder#withLocales(Locales...)}
   * to ease future support for multiple languages.
   */
  @Deprecated(since = "0.90.0", forRemoval = true)
  public static Label fromResources(String resourceIdentifier, String resourceName) {
    try {
      return new Label(resourceName, findTitleLabel(resourceIdentifier, resourceName),
          findDescriptionLabel(resourceIdentifier, resourceName));
    } catch (Exception e) {
      LOG.error("Could not find resource " + resourceIdentifier);
      return new Label(resourceName, "", "");
    }
  }

  /**
   * Creates a new label only with an internal id.
   * Static properties require a fully-specified label, see {@link #from(String, String, String)}
   *
   *
   * @param internalId The internal identifier of the element, e.g., "latitude-field-mapping"
   * @return Label
   */
  public static Label withId(String internalId) {
    return new Label(internalId, "", "");
  }

  /**
   * Creates a label with the string value of an enum.
   * Static properties require a fully-specified label, see {@link #from(String, String, String)}
   * @param internalId The internal identifier of the element, e.g., "LATITUDE-FIELD-MAPPING"
   * @return
   */
  public static Label withId(Enum<?> internalId) {
    return new Label(internalId.name(), "", "");
  }

  @Deprecated
  /**
   *  @deprecated Externalize labels by using
   *  {@link org.apache.streampipes.sdk.builder.AbstractProcessingElementBuilder#withLocales(Locales...)}
   *  to ease future support for multiple languages.
   */
  public static Label withTitle(String label, String description) {
    return new Label("", label, description);
  }

  public static Label empty() {
    return new Label("", "", "");
  }

  private static String findTitleLabel(String resourceIdentifier, String resourceName) throws Exception {
    return loadProperties(resourceIdentifier).getProperty(makeResourceId(resourceName, true));
  }

  private static String findDescriptionLabel(String resourceIdentifier, String resourceName) throws Exception {
    return loadProperties(resourceIdentifier).getProperty(makeResourceId(resourceName, false));
  }

  private static String makeResourceId(String resourceName, Boolean titleType) {
    return resourceName + "." + (titleType ? "title" : "description");
  }

  private static Properties loadProperties(String filename) throws IOException {
    URL url = Resources.asUrl(filename);
    final Properties props = new Properties();

    props.load(url.openStream());
    return props;
  }

}
