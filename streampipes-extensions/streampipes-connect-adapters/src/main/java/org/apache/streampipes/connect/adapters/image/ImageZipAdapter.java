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

package org.apache.streampipes.connect.adapters.image;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.adapters.image.stream.ImageStreamAdapter;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ImageZipAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(ImageStreamAdapter.class);

  /* Controls whether the adapter is still running or not */
  private boolean running;

  private Thread task;

  /**
   * First extracts the user input and then starts a thread publishing events with images in the zip file
   *
   * @param collector is used to pre-process and publish events on message broker
   * @param extractor to extract configurations
   * @param infinite  Describes if the replay should be restarted when it is finished or not
   */
  public void start(IEventCollector collector,
                    IStaticPropertyExtractor extractor,
                    boolean infinite) throws AdapterException {
    Integer timeBetweenReplay = extractor.singleValueParameter(ImageZipUtils.INTERVAL_KEY, Integer.class);
    String zipFileUrl = extractor.selectedFilename(ImageZipUtils.ZIP_FILE_KEY);
    ZipFileImageIterator zipFileImageIterator;
    try {
      zipFileImageIterator = new ZipFileImageIterator(zipFileUrl, infinite);
    } catch (IOException e) {
      throw new AdapterException("Error while reading images in the zip file");
    }

    running = true;

    task = new Thread(() -> {
      while (running && zipFileImageIterator.hasNext()) {

        try {
          String image = zipFileImageIterator.next();

          Map<String, Object> result = new HashMap<>();
          result.put(ImageZipUtils.TIMESTAMP, System.currentTimeMillis());
          result.put(ImageZipUtils.IMAGE, image);
          collector.collect(result);
        } catch (IOException e) {
          LOG.error("Error while reading an image from the zip file " + e.getMessage());
        }

        try {
          TimeUnit.MILLISECONDS.sleep(timeBetweenReplay);
        } catch (InterruptedException e) {
          LOG.error("Error while waiting for next replay round" + e.getMessage());
        }
      }
    });
    task.start();
  }

  /**
   * Stops the running thread that publishes the images
   */
  public void stop() {
    task.interrupt();
    running = false;
  }
}
