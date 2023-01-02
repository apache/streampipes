/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.extensions.management.connect.adapter;

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.SpecificAdapterSetDescription;
import org.apache.streampipes.model.grounding.EventGrounding;

public class GroundingService {

  public static String extractTopic(AdapterDescription adapterDescription) {
    EventGrounding eventGrounding = getEventGrounding(adapterDescription);
    return eventGrounding.getTransportProtocol().getTopicDefinition().getActualTopicName();
  }

  private static EventGrounding getEventGrounding(AdapterDescription adapterDescription) {
    EventGrounding eventGrounding;

    if (adapterDescription instanceof SpecificAdapterSetDescription) {
      eventGrounding = ((SpecificAdapterSetDescription) adapterDescription).getDataSet().getEventGrounding();
    } else if (adapterDescription instanceof GenericAdapterSetDescription) {
      eventGrounding = ((GenericAdapterSetDescription) adapterDescription).getDataSet().getEventGrounding();
    } else {
      eventGrounding = adapterDescription.getEventGrounding();
    }

    return eventGrounding;
  }

}
