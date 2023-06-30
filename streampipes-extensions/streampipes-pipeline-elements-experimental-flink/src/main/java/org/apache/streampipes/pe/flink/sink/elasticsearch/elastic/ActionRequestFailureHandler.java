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

package org.apache.streampipes.pe.flink.sink.elasticsearch.elastic;

import org.apache.flink.annotation.PublicEvolving;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.DocWriteRequest;

import java.io.Serializable;

@PublicEvolving
public interface ActionRequestFailureHandler extends Serializable {

  /**
   * Handle a failed {@link ActionRequest}.
   *
   * @param action         the {@link ActionRequest} that failed due to the failure
   * @param failure        the cause of failure
   * @param restStatusCode the REST status code of the failure (-1 if none can be retrieved)
   * @param indexer        request indexer to re-add the failed action, if intended to do so
   * @throws Throwable if the sink should fail on this failure, the implementation should rethrow
   *                   the exception or a custom one
   */
  void onFailure(DocWriteRequest action, Throwable failure, int restStatusCode, RequestIndexer indexer)
      throws Throwable;
}
