/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.management;

import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.AdapterRegistry;
import org.streampipes.connect.adapter.model.generic.Format;
import org.streampipes.connect.adapter.model.generic.Protocol;
import org.streampipes.connect.init.AdapterDeclarerSingleton;
import org.streampipes.container.api.ResolvesContainerProvidedOptions;

import java.util.Map;

public class RuntimeResovable {
    private static final String SP_NS =  "https://streampipes.org/vocabulary/v1/";


    public static ResolvesContainerProvidedOptions getRuntimeResolvableFormat(String id) throws IllegalArgumentException {
        id = id.replaceAll("sp:", SP_NS);
        Map<String, Format> allFormats = AdapterRegistry.getAllFormats();

        if (allFormats.containsKey(id)) {
            return (ResolvesContainerProvidedOptions) allFormats.get(id);
        } else {
            return null;
        }
    }

     public static ResolvesContainerProvidedOptions getRuntimeResolvableAdapter(String id) throws IllegalArgumentException {
        id = id.replaceAll("sp:", SP_NS);
        Map<String, Adapter> allAdapters = AdapterDeclarerSingleton.getInstance().getAllAdaptersMap();
        Map<String, Protocol> allProtocols =  AdapterDeclarerSingleton.getInstance().getAllProtocolsMap();

        if (allAdapters.containsKey(id)) {
            return (ResolvesContainerProvidedOptions) allAdapters.get(id);
        } else if (allProtocols.containsKey(id)) {
            return (ResolvesContainerProvidedOptions) allProtocols.get(id);
        } else {
            throw new IllegalArgumentException("Could not find adapter with id " + id);
        }
    }

}
