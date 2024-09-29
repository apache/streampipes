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
package org.apache.streampipes.rest.extensions;

import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.rest.extensions.html.HTMLGenerator;
import org.apache.streampipes.rest.extensions.html.JSONGenerator;
import org.apache.streampipes.rest.extensions.html.page.WelcomePageGenerator;

import java.util.Collection;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class WelcomePage {

  @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
  public String getWelcomePageHtml() {
    WelcomePageGenerator welcomePage = getWelcomePageGenerator();
    HTMLGenerator html = new HTMLGenerator(welcomePage.buildUris());
    return html.buildHtml();
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public String getWelcomePageJson() {
    WelcomePageGenerator welcomePage = getWelcomePageGenerator();
    JSONGenerator json = new JSONGenerator(welcomePage.buildUris());
    return json.buildJson();
  }

  private WelcomePageGenerator getWelcomePageGenerator() {
    return new WelcomePageGenerator(DeclarersSingleton.getInstance().getBaseUri(), getPipelineElements(),
            getAdapters());
  }

  private Collection<IStreamPipesPipelineElement<?>> getPipelineElements() {
    return DeclarersSingleton.getInstance().getDeclarers().values();
  }

  private Collection<StreamPipesAdapter> getAdapters() {
    return DeclarersSingleton.getInstance().getAdapters();
  }
}
