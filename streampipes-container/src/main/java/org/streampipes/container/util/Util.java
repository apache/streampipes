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

package org.streampipes.container.util;

import com.google.gson.Gson;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.model.Response;

public class Util {
    public static String getInstanceId(String url, String type, String elemntId) {
        return url.replace(DeclarersSingleton.getInstance().getBaseUri() + type + "/" + elemntId + "/", "");
    }

    public static String toResponseString(String elementId, boolean success) {
        return toResponseString(elementId, success, "");
    }

    public static Response fromResponseString(String s) {
        Gson gson = new Gson();
        Response result = gson.fromJson(s, Response.class);

        if (result.getElementId() == null) {
            return null;
        } else {
            return result;
        }

    }

    public static String toResponseString(String elementId, boolean success, String optionalMessage) {
        return new Response(elementId, success, optionalMessage).toString();
    }

    public static String toResponseString(Response response) {
        Gson gson = new Gson();
        return gson.toJson(response);
    }
}
