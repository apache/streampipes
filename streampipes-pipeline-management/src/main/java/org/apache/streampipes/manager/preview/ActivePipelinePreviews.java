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
package org.apache.streampipes.manager.preview;

import org.apache.streampipes.model.base.NamedStreamPipesEntity;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public enum ActivePipelinePreviews {

  INSTANCE;

  private final Map<String, List<NamedStreamPipesEntity>> activePreviews;
  private final Map<String, ScheduledFuture<?>> pendingReleases;
  private final Map<String, ScheduledFuture<?>> lifetimeLimits;
  private final ScheduledExecutorService releaseScheduler;

  ActivePipelinePreviews() {
    this.activePreviews = new ConcurrentHashMap<>();
    this.pendingReleases = new ConcurrentHashMap<>();
    this.lifetimeLimits = new ConcurrentHashMap<>();
    this.releaseScheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
      var thread = new Thread(runnable, "pipeline-preview-release");
      thread.setDaemon(true);
      return thread;
    });
  }

  public void addActivePreview(String previewId,
                               List<NamedStreamPipesEntity> activePreviews) {
    this.activePreviews.put(previewId, activePreviews);
  }

  /**
   * Removes the preview atomically, so that concurrent stop requests detach its graphs only once.
   *
   * @return the graphs of the removed preview, or empty if the preview was not (or no longer) active
   */
  public Optional<List<NamedStreamPipesEntity>> removePreview(String previewId) {
    cancelScheduledRelease(previewId);
    var lifetimeLimit = lifetimeLimits.remove(previewId);
    if (lifetimeLimit != null) {
      lifetimeLimit.cancel(false);
    }
    return Optional.ofNullable(this.activePreviews.remove(previewId));
  }

  /**
   * Runs {@code release} after {@code maxDuration}, whether or not a client is attached.
   * Only removing the preview cancels it.
   */
  public void limitLifetime(String previewId,
                            Duration maxDuration,
                            Runnable release) {
    var future = releaseScheduler.schedule(() -> {
      lifetimeLimits.remove(previewId);
      release.run();
    }, maxDuration.toMillis(), TimeUnit.MILLISECONDS);
    var previous = lifetimeLimits.put(previewId, future);
    if (previous != null) {
      previous.cancel(false);
    }
  }

  /**
   * Runs {@code release} after {@code delay} unless {@link #cancelScheduledRelease(String)} is called first.
   * Replaces a release that is already scheduled for the preview.
   */
  public void scheduleRelease(String previewId,
                              Duration delay,
                              Runnable release) {
    cancelScheduledRelease(previewId);
    var future = releaseScheduler.schedule(() -> {
      pendingReleases.remove(previewId);
      release.run();
    }, delay.toMillis(), TimeUnit.MILLISECONDS);
    pendingReleases.put(previewId, future);
  }

  public void cancelScheduledRelease(String previewId) {
    var future = pendingReleases.remove(previewId);
    if (future != null) {
      future.cancel(false);
    }
  }

  public Optional<List<NamedStreamPipesEntity>> getInvocationGraphs(String previewId) {
    return Optional.ofNullable(this.activePreviews.get(previewId));
  }

  public boolean isActive(String previewId) {
    return this.activePreviews.containsKey(previewId);
  }
}
