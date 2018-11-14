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

package org.streampipes.connect.utils;

import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
import org.streampipes.model.connect.adapter.*;
import org.streampipes.serializers.jsonld.JsonLdTransformer;
import org.streampipes.vocabulary.StreamPipes;

import java.lang.reflect.InvocationTargetException;

public class Utils {

    public static String getMinimalStreamAdapterJsonLD() {
        return getMinimalAdapterJsonLD(new GenericAdapterStreamDescription(), StreamPipes.GENERIC_ADAPTER_SET_DESCRIPTION);
    }

    public static String getMinimalSetAdapterJsonLD() {
        return getMinimalAdapterJsonLD(new GenericAdapterSetDescription(), StreamPipes.GENERIC_ADAPTER_SET_DESCRIPTION);
    }

    private static String getMinimalAdapterJsonLD(AdapterDescription asd, String rootElement) {
        String id = "http://t.de/";
        asd.setUri(id);
        asd.setId(id);

        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer(rootElement);

        try {
            return org.streampipes.commons.Utils.asString(jsonLdTransformer.toJsonLd(asd));
        } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (InvalidRdfException e) {
            e.printStackTrace();
        }

        return "";
    }


    public static AdapterStreamDescription getMinimalStreamAdapter() {
        AdapterStreamDescription result = new GenericAdapterStreamDescription();
        String id = "http://t.de/";
        result.setUri(id);
        result.setId(id);

        return result;
    }

    public static AdapterSetDescription getMinimalSetAdapter() {
        AdapterSetDescription result = new GenericAdapterSetDescription();
        String id = "http://t.de/";
        result.setUri(id);
        result.setId(id);

        return result;
    }

}
