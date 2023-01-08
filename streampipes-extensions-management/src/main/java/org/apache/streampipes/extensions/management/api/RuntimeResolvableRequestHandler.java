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

package org.apache.streampipes.extensions.management.api;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.extensions.api.runtime.ResolvesContainerProvidedOptions;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.SelectionStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;

import java.util.List;

public class RuntimeResolvableRequestHandler {

  // for backwards compatibility
  public RuntimeOptionsResponse handleRuntimeResponse(ResolvesContainerProvidedOptions resolvesOptions,
                                                      RuntimeOptionsRequest req) throws SpConfigurationException {
    List<Option> availableOptions =
        resolvesOptions.resolveOptions(req.getRequestId(),
            makeExtractor(req));

    SelectionStaticProperty sp = getConfiguredProperty(req);
    sp.setOptions(availableOptions);

    return new RuntimeOptionsResponse(req, sp);
  }

  public RuntimeOptionsResponse handleRuntimeResponse(SupportsRuntimeConfig declarer,
                                                      RuntimeOptionsRequest req) throws SpConfigurationException {
    StaticProperty result = declarer.resolveConfiguration(
        req.getRequestId(),
        makeExtractor(req));

    return new RuntimeOptionsResponse(req, result);
  }

  private SelectionStaticProperty getConfiguredProperty(RuntimeOptionsRequest req) {
    return req.getStaticProperties()
        .stream()
        .filter(p -> p.getInternalName().equals(req.getRequestId()))
        .map(p -> (SelectionStaticProperty) p)
        .findFirst()
        .get();
  }

  private StaticPropertyExtractor makeExtractor(RuntimeOptionsRequest req) {
    return StaticPropertyExtractor.from(req.getStaticProperties(),
        req.getInputStreams(),
        req.getAppId());
  }
}
