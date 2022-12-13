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

package org.apache.streampipes.sinks.databases.jvm.couchdb;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventConverter;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

public class CouchDb implements EventSink<CouchDbParameters> {

  private CouchDbClient couchDbClient;

  @Override
  public void onInvocation(CouchDbParameters parameters, EventSinkRuntimeContext runtimeContext) throws
      SpRuntimeException {
    this.couchDbClient = new CouchDbClient(new CouchDbProperties(
        parameters.getDatabaseName(),
        true,
        "http",
        parameters.getCouchDbHost(),
        parameters.getCouchDbPort(),
        parameters.getUser(),
        parameters.getPassword()
    ));
  }

  @Override
  public void onEvent(Event inputEvent) {
    couchDbClient.save(new EventConverter(inputEvent).toInputEventMap());
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    this.couchDbClient.shutdown();
  }


}
