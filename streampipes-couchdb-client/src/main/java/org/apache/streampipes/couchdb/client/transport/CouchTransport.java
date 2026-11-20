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

package org.apache.streampipes.couchdb.client.transport;

import java.util.Map;

/**
 * Sends requests to a CouchDB server. Implementations are thread-safe and pool connections.
 *
 * <p>The transport returns every response, including error statuses; mapping statuses to exceptions is done by
 * the client. Failures without a response are reported as
 * {@link org.apache.streampipes.couchdb.client.exception.CouchDbTransportException}.</p>
 */
public interface CouchTransport extends AutoCloseable {

  /**
   * Sends a request. Headers for authentication are already part of {@code headers}.
   *
   * @param request request to send
   * @param headers additional request headers
   * @return the response, whose body the caller closes
   */
  CouchResponse send(CouchRequest request, Map<String, String> headers);

  @Override
  void close();
}
