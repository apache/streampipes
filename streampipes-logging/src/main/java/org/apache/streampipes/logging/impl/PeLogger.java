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

import org.apache.streampipes.logging.api.Logger;

import org.slf4j.LoggerFactory;

public class PeLogger implements Logger {

  private org.slf4j.Logger logger;

  private String prefix;


  //public PeLogger(Class clazz, String correspondingPipeline, String peUri, PeConfig peConfig){
  public PeLogger(Class clazz, String correspondingPipeline, String peUri) {
    this.prefix = "USERLOG" + " - "
        // + "serviceName: " + peConfig.getName() + " - "
        + "correspondingPipeline: " + correspondingPipeline + " - "
        + "peURI: " + peUri + " - ";

    logger = LoggerFactory.getLogger(clazz);
  }

  public void info(String s) {
    logger.info(prefix + s);
  }

  public void trace(String s) {
    logger.trace(prefix + s);
  }

  public void debug(String s) {
    logger.debug(prefix + s);
  }

  public void error(String s) {
    logger.error(prefix + s);
  }

  public void warn(String s) {
    logger.warn(prefix + s);
  }

}
