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

package org.apache.streampipes.extensions.management.connect.adapter.model.generic;

import org.apache.streampipes.extensions.api.connect.IProtocol;
import org.apache.streampipes.extensions.management.connect.adapter.Adapter;
import org.apache.streampipes.model.connect.adapter.GenericAdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericDataSetAdapter extends GenericAdapter<GenericAdapterSetDescription> {

  public static final String ID = GenericAdapterSetDescription.ID;

  Logger logger = LoggerFactory.getLogger(Adapter.class);

  public GenericDataSetAdapter() {
    super();
  }


  public GenericDataSetAdapter(GenericAdapterSetDescription adapterDescription, boolean debug) {
    super(adapterDescription, debug);
  }

  public GenericDataSetAdapter(GenericAdapterSetDescription adapterDescription) {
    super(adapterDescription);
  }

  @Override
  public GenericAdapterSetDescription declareModel() {
    GenericAdapterSetDescription adapterDescription = new GenericAdapterSetDescription();
//        adapterDescription.setAdapterId(GenericAdapterSetDescription.ID);
//        adapterDescription.setUri(GenericAdapterSetDescription.ID);
    adapterDescription.setAppId(GenericAdapterSetDescription.ID);
    return adapterDescription;
  }

  @Override
  public Adapter<GenericAdapterSetDescription> getInstance(GenericAdapterSetDescription adapterDescription) {
    return new GenericDataSetAdapter(adapterDescription);
  }

  @Override
  public String getId() {
    return ID;
  }

  public void stopAdapter() {
    protocol.stop();
  }

  @Override
  public GenericAdapterDescription getAdapterDescription() {
    return adapterDescription;
  }

  @Override
  public void setProtocol(IProtocol protocol) {
    this.protocol = protocol;
  }
}
