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

package org.streampipes.rest.shared.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.Utils;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
import org.streampipes.serializers.jsonld.JsonLdTransformer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class JsonLdUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonLdUtils.class);

    public static String toJsonLD(Object o) {
        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();
        String result = null;
        try {
            result = Utils.asString(jsonLdTransformer.toJsonLd(o));
        } catch (IllegalAccessException | InvocationTargetException | InvalidRdfException | ClassNotFoundException e) {
            logger.error("Could not serialize JsonLd", e);
        }

        return result;
    }

    public static <T> T fromJsonLd(String json, Class<T> clazz) {
        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();

        try {
            return jsonLdTransformer.fromJsonLd(json, clazz);
        } catch (IOException e) {
            logger.error("Could not deserialize JsonLd", e);
        }
        return null;
    }
}
