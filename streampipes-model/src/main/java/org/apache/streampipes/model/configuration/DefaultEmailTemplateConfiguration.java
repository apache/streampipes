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
package org.apache.streampipes.model.configuration;

import org.apache.streampipes.commons.resources.Resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultEmailTemplateConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultEmailTemplateConfiguration.class);

  private static final String DefaultTemplateFilename = "default-mail-template.html";

  public EmailTemplateConfig getDefaultTemplates() {
    String template = "";
    try {
      template = removeHeader(getTemplate());
    } catch (IOException e) {
      LOG.warn("Default email template could not be loaded - set this manually in the UI under Configuration->Mail");
    }
    return new EmailTemplateConfig(template);
  }

  private String getTemplate() throws IOException {
    return Resources.asString(DefaultTemplateFilename, StandardCharsets.UTF_8);
  }

  private String removeHeader(String template) {
    return Arrays.stream(template.split("\n")).skip(22).collect(Collectors.joining("\n"));
  }
}
