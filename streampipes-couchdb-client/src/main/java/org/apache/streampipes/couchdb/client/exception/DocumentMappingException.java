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

package org.apache.streampipes.couchdb.client.exception;

/**
 * A document could not be converted between JSON and its Java type by the configured codec, or a response did not
 * have the shape CouchDB documents for it.
 */
public class DocumentMappingException extends CouchDbException {

  public DocumentMappingException(String message) {
    super(message, null);
  }

  public DocumentMappingException(String message, Throwable cause) {
    super(message, cause);
  }
}
