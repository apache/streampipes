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

package org.apache.streampipes.health.monitoring;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Counts the times an extension service became available (registered and healthy).
 * {@link PipelineRecoveryBackoff} compares the generation recorded at the last failed
 * recovery attempt with the current one, so a service that comes back makes pending
 * recoveries eligible immediately instead of waiting out the exponential backoff.
 */
public enum ServiceAvailabilityGeneration {
  INSTANCE;

  private final AtomicLong generation = new AtomicLong();

  public void serviceBecameAvailable() {
    generation.incrementAndGet();
  }

  public long current() {
    return generation.get();
  }
}
