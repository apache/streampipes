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

import org.streampipes.connect.adapter.exception.AdapterException;
import org.streampipes.model.connect.adapter.*;
import org.streampipes.rest.shared.util.JsonLdUtils;

public class AdapterDeserializer {

    public static AdapterDescription getAdapterDescription(String jsonld) throws AdapterException {
        if (jsonld.contains("SpecificAdapterSetDescription")) {
            return JsonLdUtils.fromJsonLd(jsonld, SpecificAdapterSetDescription.class);
        } else if (jsonld.contains("SpecificAdapterStreamDescription")) {
            return JsonLdUtils.fromJsonLd(jsonld, SpecificAdapterStreamDescription.class);
        } else if (jsonld.contains("GenericAdapterSetDescription")) {
            return JsonLdUtils.fromJsonLd(jsonld, GenericAdapterSetDescription.class);
        } else if (jsonld.contains("GenericAdapterStreamDescription")) {
            return JsonLdUtils.fromJsonLd(jsonld, GenericAdapterStreamDescription.class);
        } else {
            throw new AdapterException("Adapter type not registerd");
        }

    }
}
