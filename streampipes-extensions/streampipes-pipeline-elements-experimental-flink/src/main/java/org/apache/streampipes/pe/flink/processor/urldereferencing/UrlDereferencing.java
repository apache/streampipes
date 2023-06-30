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

package org.apache.streampipes.pe.flink.processor.urldereferencing;

import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

public class UrlDereferencing implements FlatMapFunction<Event, Event> {

  private String urlString;
  private String appendHtml;
  private Logger logger;

  public UrlDereferencing(String urlString, String appendHtml, DataProcessorInvocation graph) {
    this.urlString = urlString;
    this.appendHtml = appendHtml;
    this.logger = graph.getLogger(UrlDereferencing.class);
  }

  @Override
  public void flatMap(Event in, Collector<Event> out) throws Exception {
    Response response;

    try {
      response = Request.Get(
          in.getFieldBySelector(urlString).getAsPrimitive().getAsString()
      ).execute();
      String body = response.returnContent().asString();

      in.addField(appendHtml, body);
    } catch (Exception e) {
      logger.error("Error while fetching data from URL: " + urlString);
      in.addField(appendHtml, "Error while fetching data from URL: " + urlString);
    }

    out.collect(in);
  }
}
