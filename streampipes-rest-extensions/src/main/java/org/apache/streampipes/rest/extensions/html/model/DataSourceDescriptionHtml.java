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

package org.apache.streampipes.rest.extensions.html.model;

import java.util.ArrayList;
import java.util.List;

public class DataSourceDescriptionHtml extends Description {

  private List<Description> streams;

  public DataSourceDescriptionHtml(String name, String description, String descriptionUrl, List<Description> streams) {
    super(name, description, descriptionUrl);
    this.streams = streams;
  }

  public DataSourceDescriptionHtml() {
    streams = new ArrayList<>();
  }

  public List<Description> getStreams() {
    return streams;
  }

  public void setStreams(List<Description> streams) {
    this.streams = streams;
  }
}
