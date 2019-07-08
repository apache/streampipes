/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.connect.adapter.preprocessing;

import org.streampipes.model.connect.adapter.GenericAdapterDescription;
import org.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.streampipes.model.schema.EventSchema;

import java.util.Arrays;
import java.util.List;

public class Util {

    public static String getLastKey(String s) {
        String[] list = s.split("\\.");
        if (list.length == 0) {
            return s;
        } else {
            return list[list.length - 1];
        }
    }

    public static List<String> toKeyArray(String s) {
        String[] split = s.split("\\.");
        if (split.length == 0) {
            return Arrays.asList(s);
        } else {
            return Arrays.asList(split);
        }
    }

    public static EventSchema getEventSchema(GenericAdapterDescription adapterDescription) {
        if(adapterDescription instanceof GenericAdapterStreamDescription) {
            return ((GenericAdapterStreamDescription) adapterDescription).getDataStream().getEventSchema();
        } else if (adapterDescription instanceof GenericAdapterSetDescription) {
            return ((GenericAdapterSetDescription) adapterDescription).getDataSet().getEventSchema();

        }
        return null;
    }
}
