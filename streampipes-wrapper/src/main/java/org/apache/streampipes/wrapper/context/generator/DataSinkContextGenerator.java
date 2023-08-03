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

package org.apache.streampipes.wrapper.context.generator;

import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.context.IContextGenerator;
import org.apache.streampipes.extensions.management.monitoring.ExtensionsLogger;
import org.apache.streampipes.extensions.management.util.RuntimeContextUtils;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.wrapper.context.SpEventSinkRuntimeContext;

public class DataSinkContextGenerator implements IContextGenerator<EventSinkRuntimeContext, DataSinkInvocation> {

  @Override
  public EventSinkRuntimeContext makeContext(DataSinkInvocation invocation) {
    return new SpEventSinkRuntimeContext(
        invocation.getCorrespondingUser(),
        RuntimeContextUtils.makeConfigExtractor(),
        RuntimeContextUtils.makeStreamPipesClient(),
        new ExtensionsLogger(invocation.getElementId()));
  }
}
