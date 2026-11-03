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

package org.apache.streampipes.dataexplorer;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.couchdb.client.Attachments;
import org.apache.streampipes.couchdb.client.CouchServer;
import org.apache.streampipes.couchdb.client.DatabaseOptions;
import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventProperty;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class ImageStore {
  private static final String DB_NAME = "images";

  private final List<EventProperty> imageProperties;
  private final CouchServer couchServer;
  private final Attachments attachments;

  public ImageStore(DatasetMetadata measure,
                    Environment environment) {
    this(measure, from(environment));
  }

  ImageStore(DatasetMetadata measure,
             CouchServer couchServer) {
    this.couchServer = couchServer;
    var database = couchServer.database(DB_NAME, DatabaseOptions.createIfMissing());
    database.create();
    this.attachments = database.attachments();
    this.imageProperties = ImageStoreUtils.getImageProperties(measure);
  }

  private static CouchServer from(Environment env) {
    return CouchServer.builder()
        .url(env.getCouchDbProtocol().getValueOrDefault(),
            env.getCouchDbHost().getValueOrDefault(),
            env.getCouchDbPort().getValueOrDefault())
        .credentials(env.getCouchDbUsername().getValueOrDefault(), env.getCouchDbPassword().getValueOrDefault())
        .build();
  }

  public void onEvent(Event event) throws SpRuntimeException {
    this.imageProperties.forEach(eventProperty -> {
      String imageDocId = UUID.randomUUID().toString();
      String image = event.getFieldByRuntimeName(eventProperty.getRuntimeName()).getAsPrimitive().getAsString();

      byte[] data = Base64.decodeBase64(image);
      storeImage(data, imageDocId);
      event.updateFieldBySelector("s0::" + eventProperty.getRuntimeName(), imageDocId);
    });
  }

  public void storeImage(byte[] imageBytes,
                         String imageDocId) {
    this.attachments.put(imageDocId, null, imageDocId, "image/jpeg", imageBytes);
  }

  public void close() throws IOException {
    this.couchServer.close();
  }
}
