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
package org.apache.streampipes.integration.adapters;

import org.apache.streampipes.connect.iiot.protocol.stream.pulsar.PulsarProtocol;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.management.connect.AdapterUtils;
import org.apache.streampipes.extensions.management.connect.adapter.Adapter;
import org.apache.streampipes.extensions.management.connect.adapter.preprocessing.elements.DebugAdapterSink;
import org.apache.streampipes.extensions.management.init.DeclarersSingleton;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;

import java.util.List;
import java.util.Map;

public abstract class AdapterTesterBase implements AutoCloseable {
  Adapter adapter;

  public Adapter startAdapter(AdapterDescription adapterDescription) throws AdapterException {
    DeclarersSingleton.getInstance().add(new PulsarProtocol());
    Adapter adapter = (Adapter) AdapterUtils.setAdapter(adapterDescription);
    adapter.startAdapter();
    this.adapter = adapter;
    return adapter;
  }

  public abstract void startAdapterService() throws Exception;

  public abstract AdapterDescription prepareAdapter() throws Exception;

  public abstract List<Map<String, Object>> generateData() throws Exception;

  public abstract void validateData(List<Map<String, Object>> data) throws Exception;

  public Map<String, Object> takeEvent() throws InterruptedException {
    return ((DebugAdapterSink) adapter.getAdapterPipeline().getPipelineSink()).takeEvent();
  }

  public void stopAdapter() throws AdapterException {
    if (adapter != null) {
      adapter.stopAdapter();
    }
  }

}
