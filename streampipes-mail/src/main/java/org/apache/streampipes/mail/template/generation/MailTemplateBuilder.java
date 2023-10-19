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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MailTemplateBuilder {

  private final String outerTemplate;
  private String innerPart;
  private final Map<String, String> placeholders;

  private MailTemplateBuilder(String outerTemplate) {
    this.outerTemplate = outerTemplate;
    this.placeholders = new HashMap<>();
  }

  public static MailTemplateBuilder create(String outerTemplate) {
    return new MailTemplateBuilder(outerTemplate);
  }

  public MailTemplateBuilder withInnerPart(MailTemplatePart innerPart) {
    try {
      this.innerPart = innerPart.getContent();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  public MailTemplateBuilder withInnerPart(String content) {
    this.innerPart = content;
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
    String fullTemplate = getAndApplyPlaceholders(outerTemplate);

    for (String key : placeholders.keySet()) {
      String placeholder = makeKey(key);
      fullTemplate = fullTemplate.replaceAll(placeholder, placeholders.get(key));
    }

    return fullTemplate;
  }

  private String getAndApplyPlaceholders(String outerTemplate) throws IOException {
    return applyInnerTemplate(outerTemplate);
//    //String partContentAsString = partContent.getContent();
//    for (String innerPartKey : innerParts.keySet()) {
//      String templateKey = makeKey(innerPartKey);
//      if (hasPlaceholder(partContentAsString, templateKey)) {
//        partContentAsString =
//            partContentAsString.replaceAll(templateKey, getAndApplyPlaceholders(innerParts.get(innerPartKey)));
//

    //return outerTemplate;
  }

  private String applyInnerTemplate(String content) {
    return content.replaceAll(makeKey(DefaultPlaceholders.INNER.key()), innerPart);
  }

  private boolean hasPlaceholder(String content, String placeholder) {
    return content.contains(placeholder);
  }

  private String makeKey(String key) {
    return "###" + key + "###";
  }
}
