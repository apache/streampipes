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
package org.apache.streampipes.storage.api.core;

import org.apache.streampipes.storage.api.pipeline.ICompactPipelineTemplateStorage;
import org.apache.streampipes.storage.api.pipeline.IDataSinkStorage;
import org.apache.streampipes.storage.api.pipeline.IDataStreamStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementTemplateStorage;
import org.apache.streampipes.storage.api.system.ICertificateStorage;
import org.apache.streampipes.storage.api.system.IExtensionsServiceConfigurationStorage;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;
import org.apache.streampipes.storage.api.system.IGenericStorage;
import org.apache.streampipes.storage.api.system.ITransformationScriptTemplateStorage;

public interface INoSqlStorage {

  IGenericStorage getGenericStorage();

  IPipelineElementTemplateStorage getPipelineElementTemplateStorage();

  IPipelineElementDescriptionStorage getPipelineElementDescriptionStorage();

  IDataSinkStorage getDataSinkStorage();

  IDataStreamStorage getDataStreamStorage();

  IExtensionsServiceStorage getExtensionsServiceStorage();

  IExtensionsServiceConfigurationStorage getExtensionsServiceConfigurationStorage();

  ICompactPipelineTemplateStorage getPipelineTemplateStorage();

  ICertificateStorage getCertificateStorage();

  ITransformationScriptTemplateStorage getTransformationScriptTemplateStorage();
}
