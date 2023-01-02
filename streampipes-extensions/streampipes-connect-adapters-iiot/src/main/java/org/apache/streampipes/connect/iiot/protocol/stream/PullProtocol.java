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
package org.apache.streampipes.connect.iiot.protocol.stream;

import org.apache.streampipes.extensions.api.connect.IAdapterPipeline;
import org.apache.streampipes.extensions.api.connect.IFormat;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.SendToPipeline;
import org.apache.streampipes.extensions.management.connect.adapter.model.generic.Protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class PullProtocol extends Protocol {

  private ScheduledExecutorService scheduler;

  private Logger logger = LoggerFactory.getLogger(PullProtocol.class);

  private long interval;


  public PullProtocol() {
  }

  public PullProtocol(IParser parser, IFormat format, long interval) {
    super(parser, format);
    this.interval = interval;
  }

  @Override
  public void run(IAdapterPipeline adapterPipeline) {
    final Runnable errorThread = () -> {
      executeProtocolLogic(adapterPipeline);
    };


    scheduler = Executors.newScheduledThreadPool(1);
    scheduler.schedule(errorThread, 0, TimeUnit.MILLISECONDS);

  }


  private void executeProtocolLogic(IAdapterPipeline adapterPipeline) {
    final Runnable task = () -> {

      format.reset();
      SendToPipeline stk = new SendToPipeline(format, adapterPipeline);
      try {
        InputStream data = getDataFromEndpoint();
        if (data != null) {
          parser.parse(data, stk);
        } else {
          logger.warn("Could not receive data from Endpoint. Try again in " + interval + " seconds.");
        }
      } catch (ParseException e) {
        logger.error("Error while parsing: " + e.getMessage());
      }


    };

    scheduler = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> handle = scheduler.scheduleAtFixedRate(task, 1, interval, TimeUnit.SECONDS);
    try {
      handle.get();
    } catch (ExecutionException e) {
      logger.error("Error", e);
    } catch (InterruptedException e) {
      logger.error("Error", e);
    }
  }

  @Override
  public void stop() {
    scheduler.shutdownNow();
  }

  abstract InputStream getDataFromEndpoint() throws ParseException;
}
