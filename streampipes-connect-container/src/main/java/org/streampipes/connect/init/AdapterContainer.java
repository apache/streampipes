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

import org.streampipes.rest.shared.serializer.GsonClientModelProvider;
import org.streampipes.rest.shared.serializer.GsonWithIdProvider;
import org.streampipes.rest.shared.serializer.GsonWithoutIdProvider;
import org.streampipes.rest.shared.serializer.JsonLdProvider;

import java.util.HashSet;
import java.util.Set;

public abstract class AdapterContainer {

        protected static Set<Class<?>> getApiClasses() {
        Set<Class<?>> allClasses = new HashSet<>();

        // Serializers
        allClasses.add(GsonWithIdProvider.class);
        allClasses.add(GsonWithoutIdProvider.class);
        allClasses.add(GsonClientModelProvider.class);
        allClasses.add(JsonLdProvider.class);

        return allClasses;
    }
}
