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
package org.apache.streampipes.mail.template.generation;

import org.apache.streampipes.mail.template.part.MailTemplatePart;
import org.apache.streampipes.mail.utils.MailUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MailTemplateBuilder {

  private final MailTemplatePart outerPart;
  private final Map<String, MailTemplatePart> innerParts;
  private final Map<String, String> placeholders;

  private MailTemplateBuilder(MailTemplatePart outerPart) {
    this.outerPart = outerPart;
    this.innerParts = new HashMap<>();
    this.placeholders = new HashMap<>();
  }

  public static MailTemplateBuilder create(MailTemplatePart outerPart) {
    return new MailTemplateBuilder(outerPart);
  }

  public MailTemplateBuilder addSubpart(String placeholder, MailTemplatePart templatePart) {
    this.innerParts.put(placeholder, templatePart);

    return this;
  }

  public MailTemplateBuilder addSubpart(DefaultPlaceholders placeholder, MailTemplatePart templatePart) {
    return addSubpart(placeholder.key(), templatePart);

  }

  public MailTemplateBuilder addSubparts(Map<String, MailTemplatePart> templateParts) {
    this.innerParts.putAll(templateParts);
    return this;
  }

  public MailTemplateBuilder withPlaceholders(Map<String, String> placeholders) {
    this.placeholders.putAll(placeholders);

    return this;
  }

  public MailTemplateBuilder withPlaceholder(String placeholder, String content) {
    this.placeholders.put(placeholder, content);

    return this;
  }

  public MailTemplateBuilder withPlaceholder(DefaultPlaceholders placeholder, String content) {
    return withPlaceholder(placeholder.key(), content);
  }

  public String generateHtmlTemplate() throws IOException {
    String fullTemplate = getAndApplyPlaceholders(outerPart);

    for (String key : placeholders.keySet()) {
      String placeholder = makeKey(key);
      fullTemplate = fullTemplate.replaceAll(placeholder, placeholders.get(key));
    }

    return fullTemplate;
  }

  private String readFileContentsToString(MailTemplatePart part) throws IOException {
    return MailUtils.readResourceFileToString(part.getTemplateFilename());
  }

  private String getAndApplyPlaceholders(MailTemplatePart partContent) throws IOException {
    String partContentAsString = readFileContentsToString(partContent);
    for (String innerPartKey : innerParts.keySet()) {
      String templateKey = makeKey(innerPartKey);
      if (hasPlaceholder(partContentAsString, templateKey)) {
        partContentAsString =
            partContentAsString.replaceAll(templateKey, getAndApplyPlaceholders(innerParts.get(innerPartKey)));
      }
    }

    return partContentAsString;
  }

  private boolean hasPlaceholder(String content, String placeholder) {
    return content.contains(placeholder);
  }

  private String makeKey(String key) {
    return "###" + key + "###";
  }


}
