/*
 * Copyright 2018 FZI Forschungszentrum Informatik
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
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.rest.shared.util.JsonLdUtils;

import java.util.Set;

public class AdapterDeserializer {

    public static AdapterDescription getAdapterDescription(String jsonld) throws AdapterException {
        Set<String> adapterIds = AdapterRegistry.getAllAdapters().keySet();

        AdapterDescription result = null;

        for (String key : adapterIds) {
            if (jsonld.contains(key)) {
                Adapter adapter = AdapterRegistry.getAllAdapters().get(key);
                AdapterDescription resultDescription = adapter.declareModel();

                result = JsonLdUtils.fromJsonLd(jsonld, resultDescription.getClass());

            }
        }

        if (result == null) {
           throw new AdapterException("Json-Ld of adapter description could not be deserialized: " + jsonld);
        }

        return result;
    }
}
