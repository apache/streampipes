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
package org.apache.streampipes.mail.template;

import org.apache.streampipes.mail.template.generation.DefaultPlaceholders;
import org.apache.streampipes.mail.template.generation.MailTemplateBuilder;
import org.apache.streampipes.mail.template.part.BaseUrlPart;
import org.apache.streampipes.mail.template.part.LogoPart;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.google.common.base.Charsets;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMailTemplate {

  protected abstract String getTitle();

  protected abstract String getPreHeader();

  protected abstract void addPlaceholders(Map<String, String> placeholders);

  protected abstract void configureTemplate(MailTemplateBuilder builder);

  public String generateTemplate() throws IOException {
    Map<String, String> placeholders = new HashMap<>();
    addPlaceholders(placeholders);

    var template = StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getSpCoreConfigurationStorage()
        .get()
        .getEmailTemplateConfig()
        .getTemplate();

    var builder = MailTemplateBuilder.create(template)
        .withPlaceholder(DefaultPlaceholders.TITLE, getTitle())
        .withPlaceholder(DefaultPlaceholders.PREHEADER, getPreHeader())
        .withPlaceholder(DefaultPlaceholders.LOGO, new LogoPart().generate())
        .withPlaceholder(DefaultPlaceholders.BASE_URL, new BaseUrlPart().generate())
        .withPlaceholders(placeholders);

    configureTemplate(builder);
    return builder.generateHtmlTemplate();
  }

  protected String encodeUrlPart(String content) {
    return URLEncoder.encode(content, Charsets.UTF_8);
  }
}
