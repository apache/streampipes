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

package org.apache.streampipes.connect.management.management;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.storage.couchdb.impl.AdapterInstanceStorageImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourcesManagement {

  private final Logger logger = LoggerFactory.getLogger(SourcesManagement.class);

  private final AdapterInstanceStorageImpl adapterInstanceStorage;


  public SourcesManagement(AdapterInstanceStorageImpl adapterStorage) {
    this.adapterInstanceStorage = adapterStorage;
  }

  public SourcesManagement() {
    this(new AdapterInstanceStorageImpl());
  }

  public static SpDataStream updateDataStream(AdapterDescription adapterDescription,
                                              SpDataStream oldDataStream) {

    oldDataStream.setName(adapterDescription.getName());

    // Update event schema
    var newEventSchema = ((AdapterStreamDescription) adapterDescription).getDataStream().getEventSchema();
    oldDataStream.setEventSchema(newEventSchema);

    return oldDataStream;
  }

  public SpDataStream createAdapterDataStream(AdapterDescription adapterDescription,
                                              String dataStreamElementId) {

    var ds = ((AdapterStreamDescription) adapterDescription).getDataStream();
    ds.setEventGrounding(new EventGrounding(adapterDescription.getEventGrounding()));

    ds.setElementId(dataStreamElementId);
    ds.setName(adapterDescription.getName());
    ds.setDescription(adapterDescription.getDescription());
    ds.setCorrespondingAdapterId(adapterDescription.getElementId());
    ds.setInternallyManaged(true);

    return ds;
  }

}
