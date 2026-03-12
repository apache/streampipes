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

package org.apache.streampipes.manager.api.extensions;

import java.io.IOException;

public interface ExtensionServiceRequestManager {


  ExtensionServiceOperationResult requestContainerProvidedOptions(ExtensionServiceRequestTarget target,
                                                                  String payload) throws IOException;

  ExtensionServiceOperationResult requestMigration(ExtensionServiceRequestTarget target,
                                                   String payload) throws IOException;

  ExtensionServiceOperationResult requestDescriptionUpdate(ExtensionServiceRequestTarget target) throws IOException;

  ExtensionServiceOperationResult requestExtensionDescription(ExtensionServiceRequestTarget target) throws IOException;


  ExtensionServiceOperationResult requestFunctionStop(ExtensionServiceRequestTarget target) throws IOException;

  ExtensionServiceOperationResult requestAdapterStateChange(ExtensionServiceRequestTarget target,
                                                            String elementId,
                                                            String payload) throws IOException;

  ExtensionServiceOperationResult requestRuntimeOptions(ExtensionServiceRequestTarget target,
                                                        String payload) throws IOException;

  ExtensionServiceOperationResult requestSampleData(ExtensionServiceRequestTarget target,
                                                    String payload) throws IOException;

  ExtensionServiceOperationResult requestExtensionInstanceHealth(ExtensionServiceRequestTarget target) throws IOException;

  ExtensionServiceOperationResult requestServiceHealth(ExtensionServiceRequestTarget target) throws IOException;

  ExtensionServiceOperationResult requestPipelineElementInvocation(ExtensionServiceRequestTarget target,
                                                                   String pipelineId,
                                                                   String payload) throws IOException;

  ExtensionServiceOperationResult requestPipelineElementDetach(ExtensionServiceRequestTarget target,
                                                               String pipelineId) throws IOException;

  ExtensionServiceOperationResult requestPipelineElementAssets(ExtensionServiceRequestTarget target) throws IOException;

  ExtensionServiceOperationResult requestAdapterAssets(ExtensionServiceRequestTarget target) throws IOException;

  ExtensionServiceOperationResult requestAdapterIconAsset(ExtensionServiceRequestTarget target) throws IOException;


  ExtensionServiceOperationResult requestAdapterDocumentationAsset(ExtensionServiceRequestTarget target) throws IOException;

  ExtensionServiceOperationResult requestOutputSchema(ExtensionServiceRequestTarget target,
                                                      String payload) throws IOException;
}
