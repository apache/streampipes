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
package org.apache.streampipes.resource.management.secret;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.staticproperty.StaticProperty;

import java.util.List;

public class SecretService {

  private final SecretVisitor visitor;

  public SecretService(ISecretHandler secretHandler) {
    this.visitor = new SecretVisitor(secretHandler);
  }

  public void apply(Pipeline pipeline) {
    pipeline.getSepas().forEach(this::apply);
    pipeline.getActions().forEach(this::apply);
  }

  public void apply(AdapterDescription adapterDescription) {
    if (adapterDescription.getConfig() != null) {
      applyConfig(adapterDescription.getConfig());
    }
  }

  public void apply(List<InvocableStreamPipesEntity> graphs) {
    graphs.forEach(this::apply);
  }

  public void apply(InvocableStreamPipesEntity graph) {
    applyConfig(graph.getStaticProperties());
  }

  public void applyConfig(List<StaticProperty> staticProperties) {
    staticProperties.forEach(sp -> sp.accept(visitor));
  }

}
