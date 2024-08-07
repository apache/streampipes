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

package org.apache.streampipes.extensions.api.connect;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;

public interface StreamPipesAdapter {
  IAdapterConfiguration declareConfig();

  /**
   * Preprocesses the adapter description before the adapter is invoked.
   *
   * <p>This method is designed to allow adapters to modify the adapter description prior to invocation.
   * It is particularly useful for adapters that need to manipulate certain values internally,
   * e.g. bypassing the adapter preprocessing pipeline. An example of such an adapter is the FileReplayAdapter,
   * which manipulates timestamp values.</p>
   *
   * <p>This is a default method and does not need to be overridden unless specific preprocessing is required.</p>
   *
   * @param adapterDescription The adapter description to be preprocessed.
   */
  default void preprocessAdapterDescription(AdapterDescription adapterDescription) {};

  void onAdapterStarted(IAdapterParameterExtractor extractor,
                        IEventCollector collector,
                        IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException;

  void onAdapterStopped(IAdapterParameterExtractor extractor,
                        IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException;

  GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException;
}
