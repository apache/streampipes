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
package org.apache.streampipes.manager.template;

import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.manager.permission.PermissionManager;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.template.PipelineTemplateDescription;
import org.apache.streampipes.model.template.PipelineTemplateInvocation;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.ArrayList;
import java.util.List;

public class PipelineTemplateInvocationHandler {

  private PipelineTemplateInvocation pipelineTemplateInvocation;
  private PipelineTemplateDescription pipelineTemplateDescription;
  private String username;

  public PipelineTemplateInvocationHandler(String username, PipelineTemplateInvocation pipelineTemplateInvocation) {
    this.username = username;
    this.pipelineTemplateInvocation = pipelineTemplateInvocation;
    this.pipelineTemplateDescription = getTemplateById(pipelineTemplateInvocation.getPipelineTemplateId());
  }

  public PipelineTemplateInvocationHandler(String username, PipelineTemplateInvocation pipelineTemplateInvocation,
                                           PipelineTemplateDescription pipelineTemplateDescription) {
    this.username = username;
    this.pipelineTemplateInvocation = pipelineTemplateInvocation;
    this.pipelineTemplateDescription = pipelineTemplateDescription;
  }


  public PipelineOperationStatus handlePipelineInvocation() {
    Pipeline pipeline = new PipelineGenerator(pipelineTemplateInvocation.getDataStreamId(), pipelineTemplateDescription,
        pipelineTemplateInvocation.getKviName()).makePipeline();
    pipeline.setCreatedByUser(username);
    pipeline.setCreatedAt(System.currentTimeMillis());
    replaceStaticProperties(pipeline);
    Operations.storePipeline(pipeline);
    Permission permission = new PermissionManager().makePermission(pipeline, username);
    StorageDispatcher.INSTANCE.getNoSqlStore().getPermissionStorage().addPermission(permission);
    Pipeline storedPipeline =
        StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().getPipeline(pipeline.getPipelineId());
    return Operations.startPipeline(storedPipeline);
  }

  private void replaceStaticProperties(Pipeline pipeline) {
    pipeline.getSepas().forEach(this::replace);
    pipeline.getActions().forEach(this::replace);
  }

  private void replace(InvocableStreamPipesEntity pe) {
    List<StaticProperty> newProperties = new ArrayList<>();
    pe.getStaticProperties().forEach(sp -> {
      if (existsInCustomizedElements(pe.getDom(), sp)) {
        newProperties.add(getCustomizedElement(pe.getDom(), pe.getDom() + sp.getInternalName()));
      } else {
        newProperties.add(sp);
      }
    });
    pe.setStaticProperties(newProperties);
  }


  private StaticProperty getCustomizedElement(String dom, String internalName) {
    StaticProperty staticProperty = pipelineTemplateInvocation
        .getStaticProperties()
        .stream()
        .filter(sp -> sp.getInternalName().equals(internalName)).findFirst().get();

    staticProperty.setInternalName(staticProperty.getInternalName().replace(dom, ""));
    return staticProperty;
  }

  private boolean existsInCustomizedElements(String dom, StaticProperty staticProperty) {
    return pipelineTemplateInvocation
        .getStaticProperties()
        .stream()
        .anyMatch(sp -> sp.getInternalName().equals(dom + staticProperty.getInternalName()));
  }


  private PipelineTemplateDescription getTemplateById(String pipelineTemplateId) {
    return new PipelineTemplateGenerator().getAllPipelineTemplates().stream()
        .filter(template -> template.getAppId().equals(pipelineTemplateId)).findFirst().get();
  }
}
