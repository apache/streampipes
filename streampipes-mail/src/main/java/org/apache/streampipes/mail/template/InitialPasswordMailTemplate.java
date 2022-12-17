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
import org.apache.streampipes.mail.template.part.LinkPart;
import org.apache.streampipes.mail.template.part.MailTemplatePart;
import org.apache.streampipes.mail.utils.MailUtils;

import java.util.Map;

public class InitialPasswordMailTemplate extends AbstractMailTemplate {

  private String initialPassword;

  public InitialPasswordMailTemplate(String initialPassword) {
    this.initialPassword = initialPassword;
  }

  @Override
  protected String getTitle() {
    return "New Account created";
  }

  @Override
  protected String getPreHeader() {
    return "A new account for " + MailUtils.extractAppName() + " has been created.";
  }

  @Override
  protected void addPlaceholders(Map<String, String> placeholders) {
    placeholders.put(DefaultPlaceholders.LINK.key(), makeLink());
    placeholders.put(DefaultPlaceholders.MANUAL.key(), "<p>Your newly created password is <b>"
        + this.initialPassword
        + "</b></p><p>Click the button below to login.</p>");
    placeholders.put(DefaultPlaceholders.BUTTON_TEXT.key(), "Login");
    placeholders.put(DefaultPlaceholders.LINK_DESCRIPTION.key(),
        "If that doesn't work, copy and paste the following link in your browser:");
  }

  @Override
  protected void addTemplateParts(Map<String, MailTemplatePart> templateParts) {
    templateParts.put(DefaultPlaceholders.INNER.key(), MailTemplatePart.MAIL_TEMPLATE_INNER_BUTTON);
  }

  private String makeLink() {
    return new LinkPart("/#/login").generate();
  }
}
