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

package org.streampipes.connect.adapter.generic.sdk;

import org.streampipes.model.staticproperty.*;

import java.util.List;
import java.util.stream.Collectors;

public class ParameterExtractor {
    private List<StaticProperty> list;

    public ParameterExtractor(List<StaticProperty> list) {
        this.list = list;
    }

    public String singleValue(String internalName) {
        return (((FreeTextStaticProperty) getStaticPropertyByName(internalName))
                .getValue());
    }

    public List<String> selectedMultiValues(String internalName) {
        return ((SelectionStaticProperty) getStaticPropertyByName(internalName))
                .getOptions()
                .stream()
                .filter(Option::isSelected)
                .map(Option::getName)
                .collect(Collectors.toList());
    }

    private StaticProperty getStaticPropertyByName(String name)
    {
        for(StaticProperty p : list)
        {
            if (p.getInternalName().equals(name)) return p;
        }
        return null;
    }
}
