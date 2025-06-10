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

import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.message.SuccessMessage;

public interface IAdapterApi extends CRUDApi<String, AdapterDescription> {

  /**
   * Starts the adapter with the given elementId.
   * @param elementId The elementId of the adapter to start
   * @return a {@link SuccessMessage} indicating the success of the operation
   */
  SuccessMessage start(String elementId);

  /**
   * Stops the adapter with the given elementId.
   * @param elementId The elementId of the adapter to stop
   * @return a {@link SuccessMessage} indicating the success of the operation
   */
  SuccessMessage stop(String elementId);

  /**
   * Starts the given adapter.
   * @param element The adapter to start
   * @return a {@link SuccessMessage} indicating the success of the operation
   */
  SuccessMessage start(AdapterDescription element);

  /**
   * Stops the given adapter.
   * @param element The adapter to stop
   * @return a {@link SuccessMessage} indicating the success of the operation
   */
  SuccessMessage stop(AdapterDescription element);

}
