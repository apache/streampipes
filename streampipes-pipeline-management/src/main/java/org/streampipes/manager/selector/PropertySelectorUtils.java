/*
Copyright 2019 FZI Forschungszentrum Informatik

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
package org.streampipes.manager.selector;

import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;

import java.util.ArrayList;
import java.util.List;

public class PropertySelectorUtils {

  public static List<EventProperty> getProperties(EventSchema eventSchema) {
    return eventSchema == null ? new ArrayList<>() : eventSchema.getEventProperties();
  }
}
