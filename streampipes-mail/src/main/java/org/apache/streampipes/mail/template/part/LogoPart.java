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
package org.apache.streampipes.mail.template.part;

import j2html.attributes.Attribute;
import j2html.tags.Tag;
import org.apache.streampipes.mail.utils.MailUtils;

import static j2html.TagCreator.div;
import static j2html.TagCreator.img;

public class LogoPart extends AbstractPart {

  private static final String LOGO_PATH = "/assets/img/login/logo.png";

  private final String baseUrl;

  public LogoPart() {
    this.baseUrl = MailUtils.extractBaseUrl();
  }

  @Override
  public Tag<?> toTag() {
    return div(
            img().withSrc(baseUrl + LOGO_PATH).withClass("logo").attr(new Attribute("height", "50px")))
            .withClass("logo");
  }
}
