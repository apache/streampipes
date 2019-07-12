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

package org.streampipes.connect.init;

import org.streampipes.connect.adapter.Adapter;
import org.streampipes.connect.adapter.model.Connector;
import org.streampipes.connect.adapter.model.generic.Protocol;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AdapterDeclarerSingleton {

    private Map<String, Protocol> allProtocols;
    private Map<String, Adapter> allAdapters;

    private static AdapterDeclarerSingleton instance;

    public AdapterDeclarerSingleton() {
        this.allProtocols = new HashMap<>();
        this.allAdapters = new HashMap<>();
    }

    public static AdapterDeclarerSingleton getInstance() {
        if (AdapterDeclarerSingleton.instance == null) {
            AdapterDeclarerSingleton.instance = new AdapterDeclarerSingleton();
        }

        return AdapterDeclarerSingleton.instance;
    }

    public AdapterDeclarerSingleton add(Connector c) {

        if (c instanceof Protocol) {
            this.allProtocols.put(((Protocol) c).getId(), (Protocol) c);
        } else if (c instanceof Adapter) {
            this.allAdapters.put(((Adapter) c).getId(), (Adapter) c);
        }

        return getInstance();
    }

    public Collection<Protocol> getAllProtocols() {
        return this.allProtocols.values();
    }

    public Collection<Adapter> getAllAdapters() {
        return this.allAdapters.values();
    }

    public Protocol getProtocol(String id) {
        return getAllProtocols().stream()
                .filter(protocol -> protocol.getId().equals(id))
                .findAny()
                .orElse(null);
    }

}
