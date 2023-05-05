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

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.management.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.management.context.IAdapterRuntimeContext;
import org.apache.streampipes.model.connect.adapter.AdapterConfiguration;
import org.apache.streampipes.model.connect.adapter.IEventCollector;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.extractor.IAdapterParameterExtractor;

// TODO Rename to IAdapter once the old class can be deleted
public interface AdapterInterface {
  AdapterConfiguration declareConfig();

  void onAdapterStarted(IAdapterParameterExtractor extractor,
                        IEventCollector collector,
                        IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException;

  void onAdapterStopped(IAdapterParameterExtractor extractor,
                        IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException;

  GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException;
}
