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

package org.apache.streampipes.sdk.builder.adapter;

import org.apache.streampipes.model.connect.adapter.AdapterConfiguration;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterConfigurationBuilder {

  private AdapterDescription adapterDescription;
  private final List<Parser> supportedParsers;

  private AdapterConfigurationBuilder() {
    supportedParsers = new ArrayList<>();
  }

  public static AdapterConfigurationBuilder create() {
    return new AdapterConfigurationBuilder();
  }

  public AdapterConfiguration build() {
    return new AdapterConfiguration(adapterDescription, supportedParsers);
  }

  public AdapterConfigurationBuilder withSupportedParsers(Parser... parsers) {
    Collections.addAll(supportedParsers, parsers);
    return this;
  }


  public AdapterConfigurationBuilder withAdapterDescription(AdapterDescription adapterDescription) {
    this.adapterDescription = adapterDescription;
    return this;
  }
}
