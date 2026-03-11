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

  ExtensionServiceOperationResult requestContainerProvidedOptions(String url,
                                                                  String payload) throws IOException;

  ExtensionServiceOperationResult requestMigration(String url,
                                                   String payload) throws IOException;

  ExtensionServiceOperationResult requestDescriptionUpdate(String requestUrl) throws IOException;

  ExtensionServiceOperationResult requestExtensionDescription(String descriptionUrl) throws IOException;

  ExtensionServiceOperationResult requestFunctionStop(String endpoint) throws IOException;

  ExtensionServiceOperationResult requestAdapterStateChange(String url,
                                                            String elementId,
                                                            String payload) throws IOException;

  ExtensionServiceOperationResult requestRuntimeOptions(String url,
                                                        String payload) throws IOException;

  ExtensionServiceOperationResult requestSampleData(String workerUrl,
                                                    String payload) throws IOException;

  ExtensionServiceOperationResult requestExtensionInstanceHealth(String url) throws IOException;

  ExtensionServiceOperationResult requestServiceHealth(String url) throws IOException;

  ExtensionServiceOperationResult requestPipelineElementInvocation(String url,
                                                                   String pipelineId,
                                                                   String payload) throws IOException;

  ExtensionServiceOperationResult requestPipelineElementDetach(String url,
                                                               String pipelineId) throws IOException;

  ExtensionServiceOperationResult requestPipelineElementAssets(String url) throws IOException;

  ExtensionServiceOperationResult requestAdapterAssets(String url) throws IOException;

  ExtensionServiceOperationResult requestAdapterIconAsset(String url) throws IOException;

  ExtensionServiceOperationResult requestAdapterDocumentationAsset(String url) throws IOException;

  ExtensionServiceOperationResult requestOutputSchema(String url,
                                                      String payload) throws IOException;
}
