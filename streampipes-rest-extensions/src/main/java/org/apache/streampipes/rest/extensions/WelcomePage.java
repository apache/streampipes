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

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Collection;

@Path("/")
public class WelcomePage {

  @GET
  @Produces(MediaType.TEXT_HTML)
  public String getWelcomePageHtml() {
    WelcomePageGenerator welcomePage = getWelcomePageGenerator();
    HTMLGenerator html = new HTMLGenerator(welcomePage.buildUris());
    return html.buildHtml();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getWelcomePageJson() {
    WelcomePageGenerator welcomePage = getWelcomePageGenerator();
    JSONGenerator json = new JSONGenerator(welcomePage.buildUris());
    return json.buildJson();
  }

  private WelcomePageGenerator getWelcomePageGenerator() {
    return new WelcomePageGenerator(
        DeclarersSingleton.getInstance().getBaseUri(),
        getPipelineElements(),
        getAdapters());
  }

  private Collection<IStreamPipesPipelineElement<?>> getPipelineElements() {
    return DeclarersSingleton
        .getInstance()
        .getDeclarers()
        .values();
  }

  private Collection<StreamPipesAdapter> getAdapters() {
    return DeclarersSingleton
        .getInstance()
        .getAdapters();
  }
}
