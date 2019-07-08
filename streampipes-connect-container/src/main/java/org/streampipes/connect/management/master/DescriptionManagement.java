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

package org.streampipes.connect.management.master;

import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.AdapterRegistry;
import org.streampipes.connect.adapter.model.generic.Format;
import org.streampipes.connect.adapter.model.generic.Protocol;
import org.streampipes.model.connect.adapter.AdapterDescriptionList;
import org.streampipes.model.connect.grounding.FormatDescriptionList;
import org.streampipes.model.connect.grounding.ProtocolDescriptionList;

import java.util.Map;

public class DescriptionManagement {

    public ProtocolDescriptionList getProtocols() {
        Map<String, Protocol> allProtocols = AdapterRegistry.getAllProtocols();

        ProtocolDescriptionList result = new ProtocolDescriptionList();

        for (Protocol p : allProtocols.values()) {
           result.getList().add(p.declareModel());
        }

        return result;
    }

    public FormatDescriptionList getFormats() {
        Map<String, Format> allFormats = AdapterRegistry.getAllFormats();

        FormatDescriptionList result = new FormatDescriptionList();

        for (Format f : allFormats.values()) {
           result.getList().add(f.declareModel());
        }

        return result;
    }

    public AdapterDescriptionList getAdapters() {
        Map<String, Adapter> allAdapters = AdapterRegistry.getAllAdapters();

        AdapterDescriptionList result = new AdapterDescriptionList();

        for (Adapter a : allAdapters.values()) {
           result.getList().add(a.declareModel());
        }

        return result;
    }
}
