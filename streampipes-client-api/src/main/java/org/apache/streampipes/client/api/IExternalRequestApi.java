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

package org.apache.streampipes.client.api;

import org.apache.streampipes.client.api.external.ExternalRequest;

import java.util.List;

/**
 * Executes JSON requests against absolute external HTTP endpoints.
 *
 * <p>This API deliberately does not apply StreamPipes credentials or custom headers. Consumers that accept
 * untrusted target URIs must enforce an appropriate endpoint allow list before calling this API.
 */
public interface IExternalRequestApi {

  <T> T execute(ExternalRequest request, Class<T> responseClass);

  Object executeJson(ExternalRequest request);

  <T> List<T> getList(ExternalRequest request, Class<T> responseClass);
}
