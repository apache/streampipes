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

package org.apache.streampipes.rest.extensions.html;

import org.apache.streampipes.rest.extensions.html.model.DataSourceDescriptionHtml;
import org.apache.streampipes.rest.extensions.html.model.Description;

import org.rendersnake.HtmlCanvas;

import java.io.IOException;
import java.util.List;

import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.href;
import static org.rendersnake.HtmlAttributesFactory.name;

public class HTMLGenerator {

  private List<Description> descriptions;

  public HTMLGenerator(List<Description> descriptions) {
    this.descriptions = descriptions;
  }

  public String buildHtml() {
    HtmlCanvas html = new HtmlCanvas();
    try {
      html
          .head()
          .meta(name("viewport").content("width=device-width, initial-scale=1"))
          .macros().javascript("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js")
          .macros().stylesheet("https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css")
          .macros().javascript("https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js")
          .style().write("body {padding-top: 70px;}")._style()
          ._head()
          .body()
          .nav(class_("navbar navbar-inverse navbar-fixed-top").style("background:#0A3F54"))
          .div(class_("container"))
          .div(class_("navbar-header"))
          .a(class_("navbar-brand").style("color:white;"))
          .content("StreamPipes Pipeline Element Container")
          ._div()
          ._div()
          ._nav()
          .div(class_("container"));

      html.h4().write("This is a developer-oriented view."
          + "Navigate to 'Install Pipeline Elements' in the StreamPipes "
          + "UI to import the elements shown here.")._h4();

      for (Description description : descriptions) {

        html.h3();
        html.write(description.getName());
        html._h3();
        html.h4().write("URI: ").a(href(description.getDescriptionUrl().toString()))
            .content(description.getDescriptionUrl().toString())._h4();
        html.h4().write("Description: ").write(description.getDescription())._h4();
        if (description instanceof DataSourceDescriptionHtml) {
          DataSourceDescriptionHtml semanticEventProducerDescription = (DataSourceDescriptionHtml) description;
          for (Description agentDesc : semanticEventProducerDescription.getStreams()) {
            html.h5().b().write(agentDesc.getName())._b()._h5();
            html.h5().write(agentDesc.getDescription())._h5();

          }
        }
      }
      html._div();
      html._body();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return html.toHtml();
  }
}
