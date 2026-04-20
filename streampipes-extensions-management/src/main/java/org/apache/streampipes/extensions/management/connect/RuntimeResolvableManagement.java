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

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOptions;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.extensions.management.api.RuntimeResolvableRequestHandler;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;

public class RuntimeResolvableManagement {

  private final RuntimeResolvableRequestHandler requestHandler;

  public RuntimeResolvableManagement() {
    this(new RuntimeResolvableRequestHandler());
  }

  public RuntimeResolvableManagement(RuntimeResolvableRequestHandler requestHandler) {
    this.requestHandler = requestHandler;
  }

  public RuntimeOptionsResponse fetchConfigurations(String elementId,
                                                    RuntimeOptionsRequest runtimeOptionsRequest)
      throws SpConfigurationException {
    var adapter = RuntimeResovable.getAdapter(elementId);

    if (adapter instanceof ResolvesContainerProvidedOptions) {
      return requestHandler.handleRuntimeResponse((ResolvesContainerProvidedOptions) adapter, runtimeOptionsRequest);
    } else if (adapter instanceof SupportsRuntimeConfig) {
      return requestHandler.handleRuntimeResponse((SupportsRuntimeConfig) adapter, runtimeOptionsRequest);
    } else {
      throw new SpRuntimeException(
          "This element does not support dynamic options - is the pipeline element description up to date?");
    }
  }
}
