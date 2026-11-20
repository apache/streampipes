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

package org.apache.streampipes.storage.couchdb.impl.system;

import org.apache.streampipes.couchdb.client.Attachments;
import org.apache.streampipes.storage.api.exceptions.StorageException;
import org.apache.streampipes.storage.api.system.IImageStorage;
import org.apache.streampipes.storage.couchdb.CouchDbStores;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ImageStorageImpl implements IImageStorage {

  private static final String DB_NAME = "images";

  private final Attachments attachments;

  public ImageStorageImpl(CouchDbStores stores) {
    this.attachments = stores.database(DB_NAME).attachments();
  }

  /**
   * Reads the image completely, so that the connection goes back to the pool even if the caller never closes the
   * stream.
   */
  @Override
  public InputStream getImageBytes(String documentId) {
    return attachments.get(documentId, documentId)
        .map(attachment -> new ByteArrayInputStream(attachment.data()))
        .orElseThrow(() -> new StorageException("No image with id " + documentId));
  }
}
