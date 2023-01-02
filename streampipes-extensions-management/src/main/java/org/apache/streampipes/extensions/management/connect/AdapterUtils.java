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

import org.apache.streampipes.extensions.api.connect.IAdapter;
import org.apache.streampipes.extensions.api.connect.IProtocol;
import org.apache.streampipes.extensions.management.connect.adapter.model.generic.GenericAdapter;
import org.apache.streampipes.extensions.management.connect.adapter.model.generic.GenericDataSetAdapter;
import org.apache.streampipes.extensions.management.connect.adapter.model.generic.GenericDataStreamAdapter;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdapterUtils {
  private static final Logger logger = LoggerFactory.getLogger(AdapterUtils.class);

  public static IAdapter setAdapter(AdapterDescription adapterDescription) {
    IAdapter adapter = null;

    if (adapterDescription instanceof GenericAdapterStreamDescription) {
      adapter = (IAdapter<?>) new GenericDataStreamAdapter().getInstance(
          (GenericAdapterStreamDescription) adapterDescription);
    } else if (adapterDescription instanceof GenericAdapterSetDescription) {
      adapter = new GenericDataSetAdapter().getInstance((GenericAdapterSetDescription) adapterDescription);
    }

    IProtocol protocol = null;
    if (adapterDescription instanceof GenericAdapterSetDescription) {
      protocol = DeclarersSingleton.getInstance()
          .getProtocol(((GenericAdapterSetDescription) adapterDescription).getProtocolDescription().getAppId());
      ((GenericAdapter) adapter).setProtocol(protocol);
    }

    if (adapterDescription instanceof GenericAdapterStreamDescription) {
      protocol = DeclarersSingleton.getInstance()
          .getProtocol(((GenericAdapterStreamDescription) adapterDescription).getProtocolDescription().getAppId());
      ((GenericAdapter) adapter).setProtocol(protocol);
    }

    if (adapter == null) {
      adapter = DeclarersSingleton
          .getInstance()
          .getAdapter(adapterDescription.getAppId()).getInstance(adapterDescription);
    }

    return adapter;
  }


}
