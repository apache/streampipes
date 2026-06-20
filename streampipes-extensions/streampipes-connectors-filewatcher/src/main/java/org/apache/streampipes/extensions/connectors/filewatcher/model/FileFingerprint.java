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

package org.apache.streampipes.extensions.connectors.filewatcher.model;

import java.io.Serializable;
import java.util.Objects;

public class FileFingerprint implements Serializable {

  private long size;
  private long lastModified;
  private String contentHash;

  public FileFingerprint() {
  }

  public FileFingerprint(long size, long lastModified, String contentHash) {
    this.size = size;
    this.lastModified = lastModified;
    this.contentHash = contentHash;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public long getLastModified() {
    return lastModified;
  }

  public void setLastModified(long lastModified) {
    this.lastModified = lastModified;
  }

  public String getContentHash() {
    return contentHash;
  }

  public void setContentHash(String contentHash) {
    this.contentHash = contentHash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof FileFingerprint other)) {
      return false;
    }
    return size == other.size
        && lastModified == other.lastModified
        && Objects.equals(contentHash, other.contentHash);
  }

  @Override
  public int hashCode() {
    int result = Long.hashCode(size);
    result = 31 * result + Long.hashCode(lastModified);
    result = 31 * result + Objects.hashCode(contentHash);
    return result;
  }
}
