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

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.connect.management.util.WorkerPaths;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.management.connect.adapter.util.TransportFormatGenerator;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.AdapterStreamDescription;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.resource.management.secret.SecretProvider;
import org.apache.streampipes.sdk.helpers.SupportedProtocols;
import org.apache.streampipes.storage.couchdb.impl.AdapterInstanceStorageImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.Arrays;

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
//
//    // Update event schema
    EventSchema newEventSchema;
    if (adapterDescription instanceof AdapterStreamDescription) {
      newEventSchema = ((AdapterStreamDescription) adapterDescription).getDataStream().getEventSchema();
    } else {
      newEventSchema = ((AdapterSetDescription) adapterDescription).getDataSet().getEventSchema();
    }
    oldDataStream.setEventSchema(newEventSchema);

    return oldDataStream;
  }

  public void addSetAdapter(SpDataSet dataSet) throws AdapterException, NoServiceEndpointsAvailableException {

    AdapterSetDescription ad = (AdapterSetDescription) getAndDecryptAdapter(dataSet.getCorrespondingAdapterId());
    ad.setDataSet(dataSet);
    ad.setElementId(ad.getElementId() + "/streams/" + dataSet.getDatasetInvocationId());

    try {
      String baseUrl = WorkerPaths.findEndpointUrl(ad.getAppId());
      WorkerRestClient.invokeSetAdapter(baseUrl, ad);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param elementId
   * @param runningInstanceId
   * @throws AdapterException
   * @throws NoServiceEndpointsAvailableException
   */
  public void detachAdapter(String elementId, String runningInstanceId)
      throws AdapterException, NoServiceEndpointsAvailableException {
    AdapterSetDescription ad = (AdapterSetDescription) getAndDecryptAdapter(elementId);
    try {
      String baseUrl = WorkerPaths.findEndpointUrl(ad.getAppId());
      ad.setElementId(ad.getElementId() + "/streams/" + runningInstanceId);
      WorkerRestClient.stopSetAdapter(baseUrl, ad);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }

  public SpDataStream createAdapterDataStream(AdapterDescription adapterDescription,
                                              String dataStreamElementId) {

    SpDataStream ds;
    if (adapterDescription instanceof AdapterSetDescription) {
      ds = ((AdapterSetDescription) adapterDescription).getDataSet();
      EventGrounding eg = new EventGrounding();
      eg.setTransportProtocols(
          Arrays.asList(SupportedProtocols.kafka(),
              SupportedProtocols.jms(),
              SupportedProtocols.mqtt()));
      eg.setTransportFormats(Arrays.asList(TransportFormatGenerator.getTransportFormat()));
      ((SpDataSet) ds).setSupportedGrounding(eg);
    } else {
      ds = ((AdapterStreamDescription) adapterDescription).getDataStream();
      ds.setEventGrounding(new EventGrounding(adapterDescription.getEventGrounding()));
    }

    ds.setElementId(dataStreamElementId);
    ds.setName(adapterDescription.getName());
    ds.setDescription(adapterDescription.getDescription());
    ds.setCorrespondingAdapterId(adapterDescription.getElementId());
    ds.setInternallyManaged(true);

    return ds;
  }

  private AdapterDescription getAndDecryptAdapter(String adapterId) {
    AdapterDescription adapter = this.adapterInstanceStorage.getAdapter(adapterId);
    SecretProvider.getDecryptionService().apply(adapter);
    return adapter;
  }

}
