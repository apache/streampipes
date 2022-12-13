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

package org.apache.streampipes.logging.impl;

import java.io.Serializable;

public class EventStatisticLogger implements Serializable {


  // private String prefix;

  // public static void log(org.apache.streampipes.model.base.InvocableStreamPipesEntity graph) {
  public static void log(String name, String correspondingPipeline, String source) {
    // TODO: Uncomment when "Event statistic" should be used
        /*    String prefix =  "SYSTEMLOG EVENT STATISTIC" + " - "
                // + "serviceName: " + peConfig.getName() + " - "
                + "correspondingPipeline: " + correspondingPipeline + " - "
                + "peURI: " + source + " - ";

        org.slf4j.Logger logger = LoggerFactory.getLogger(EventStatisticLogger.class);
        logger.info(prefix + 1);
        */
  }

}
