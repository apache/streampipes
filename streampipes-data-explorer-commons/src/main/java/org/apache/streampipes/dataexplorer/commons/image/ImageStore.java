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

package org.apache.streampipes.dataexplorer.commons.image;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventProperty;

import org.apache.commons.codec.binary.Base64;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class ImageStore {

  private static final Logger LOG = LoggerFactory.getLogger(ImageStore.class);
  private static final String DB_NAME = "images";

  private List<EventProperty> imageProperties;
  private CouchDbClient couchDbClient;

  public ImageStore(DataLakeMeasure measure,
                    Environment environment) {
    this.couchDbClient = new CouchDbClient(from(environment));
    this.imageProperties = ImageStoreUtils.getImageProperties(measure);
  }

  private static CouchDbProperties from(Environment env) {
    String couchDbProtocol = env.getCouchDbProtocol().getValueOrDefault();
    String couchDbHost = env.getCouchDbHost().getValueOrDefault();
    int couchDbPort = env.getCouchDbPort().getValueOrDefault();
    String username = env.getCouchDbUsername().getValueOrDefault();
    String password = env.getCouchDbPassword().getValueOrDefault();

    return new CouchDbProperties(DB_NAME, true, couchDbProtocol,
        couchDbHost, couchDbPort, username, password);
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
    this.couchDbClient.saveAttachment(
        new ByteArrayInputStream(imageBytes),
        imageDocId,
        "image/jpeg",
        imageDocId,
        null);

  }

  public void close() throws IOException {
    this.couchDbClient.close();
  }
}
