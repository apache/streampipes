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

package org.apache.streampipes.sinks.internal.jvm.datalake;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.io.ByteArrayInputStream;

public class ImageStore {

  private static final String DB_NAME = "images";

  private CouchDbClient couchDbClient;

  public ImageStore(String couchDbProtocol,
                    String couchDbHost,
                    int couchDbPort) {
    this.couchDbClient = new CouchDbClient(props(couchDbProtocol, couchDbHost, couchDbPort));
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

  private static CouchDbProperties props(String couchDbProtocol,
                                         String couchDbHost,
                                         int couchDbPort) {
    return new CouchDbProperties(DB_NAME, true, couchDbProtocol,
            couchDbHost, couchDbPort, null, null);
  }
}
