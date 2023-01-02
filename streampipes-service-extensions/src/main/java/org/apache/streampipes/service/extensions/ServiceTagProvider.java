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

package org.apache.streampipes.service.extensions;

import org.apache.streampipes.extensions.api.connect.IAdapter;
import org.apache.streampipes.extensions.api.connect.IProtocol;
import org.apache.streampipes.extensions.api.declarer.Declarer;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.extensions.management.util.ServiceDefinitionUtil;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTags;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceTag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServiceTagProvider {

  public List<SpServiceTag> extractServiceTags() {
    var tags = new ArrayList<SpServiceTag>();
    tags.addAll(extractPipelineElementServiceTags());
    tags.addAll(extractAdapterServiceTags());

    return tags;
  }

  private List<SpServiceTag> extractPipelineElementServiceTags() {
    Collection<Declarer<?>> declarers = DeclarersSingleton.getInstance().getDeclarers().values();
    List<SpServiceTag> serviceTags = ServiceDefinitionUtil.extractAppIds(declarers);
    serviceTags.add(DefaultSpServiceTags.PE);

    return serviceTags;
  }

  private List<SpServiceTag> extractAdapterServiceTags() {
    var tags = new ArrayList<SpServiceTag>();
    Collection<IAdapter> adapters = DeclarersSingleton.getInstance().getAllAdapters();
    Collection<IProtocol> protocols = DeclarersSingleton.getInstance().getAllProtocols();
    tags.addAll(ServiceDefinitionUtil.extractAppIdsFromAdapters(adapters));
    tags.addAll(ServiceDefinitionUtil.extractAppIdsFromProtocols(protocols));
    tags.add(DefaultSpServiceTags.CONNECT_WORKER);

    return tags;
  }
}
