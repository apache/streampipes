/*
 * Copyright 2017 FZI Forschungszentrum Informatik
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
 */

package org.streampipes.sinks.databases.jvm.couchdb;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class CouchDb extends EventSink<CouchDbParameters> {

  private CouchDbClient couchDbClient;

  public CouchDb(CouchDbParameters params) {
    super(params);
  }

  @Override
  public void bind(CouchDbParameters parameters) throws SpRuntimeException {
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
  public void onEvent(Map<String, Object> event, String sourceInfo) {
    couchDbClient.save(event);
  }

  @Override
  public void discard() throws SpRuntimeException {
  }


}
