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

package org.apache.streampipes.extensions.management.connect;

import org.apache.streampipes.extensions.api.connect.Connector;
import org.apache.streampipes.extensions.api.connect.IAdapter;
import org.apache.streampipes.extensions.api.connect.IFormat;
import org.apache.streampipes.extensions.api.connect.IProtocol;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOptions;
import org.apache.streampipes.extensions.management.connect.adapter.AdapterRegistry;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;

import java.util.Map;

public class RuntimeResovable {
  private static final String SP_NS = "https://streampipes.org/vocabulary/v1/";


  public static ResolvesContainerProvidedOptions getRuntimeResolvableFormat(String id) throws IllegalArgumentException {
    id = id.replaceAll("sp:", SP_NS);
    Map<String, IFormat> allFormats = AdapterRegistry.getAllFormats();

    if (allFormats.containsKey(id)) {
      return (ResolvesContainerProvidedOptions) allFormats.get(id);
    } else {
      return null;
    }
  }

  public static Connector getAdapterOrProtocol(String id) {
    id = id.replaceAll("sp:", SP_NS);
    Map<String, IAdapter> allAdapters = DeclarersSingleton.getInstance().getAllAdaptersMap();
    Map<String, IProtocol> allProtocols = DeclarersSingleton.getInstance().getAllProtocolsMap();

    if (allAdapters.containsKey(id)) {
      return allAdapters.get(id);
    } else if (allProtocols.containsKey(id)) {
      return allProtocols.get(id);
    } else {
      throw new IllegalArgumentException("Could not find adapter with id " + id);
    }
  }
}
