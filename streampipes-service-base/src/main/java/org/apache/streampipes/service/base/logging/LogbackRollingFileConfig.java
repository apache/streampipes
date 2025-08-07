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

package org.apache.streampipes.service.base.logging;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;

public class LogbackRollingFileConfig implements SpringApplicationRunListener {

  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(LogbackRollingFileConfig.class);
  private static final String CONSOLE_LOGGER = "CONSOLE";
  private final Environment env;

  public LogbackRollingFileConfig(SpringApplication application, String[] args) {
    env = Environments.getEnvironment();
  }

  @Override
  public void contextPrepared(ConfigurableApplicationContext ctx) {
    var fileLoggingEnabled = env.getFileLoggingEnabled().getValueOrDefault();
    if (!fileLoggingEnabled) {
      return;
    }

    var logDir = env.getFileLoggingDir().getValueOrDefault();
    var logPattern = env.getFileLoggingPattern().getValueOrDefault();
    var prefix = env.getFileLoggingPrefix().getValueOrDefault();

    File dir = new File(logDir);
    if (!dir.exists() && !dir.mkdirs()) {
      System.err.println("Failed to create log directory: " + logDir);
      return;
    }

    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

    // Encoder
    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setContext(context);
    encoder.setPattern(logPattern);
    encoder.start();

    // Rolling policy
    TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
    rollingPolicy.setContext(context);
    rollingPolicy.setFileNamePattern(logDir + "/" + prefix + ".%d{yyyy-MM-dd}.log");
    rollingPolicy.setMaxHistory(7);
    rollingPolicy.setParent(new RollingFileAppender<>());
    rollingPolicy.start();

    // Rolling File Appender
    RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
    fileAppender.setContext(context);
    fileAppender.setName("FILE");
    fileAppender.setFile(logDir + "/" + prefix + ".log");
    fileAppender.setEncoder(encoder);
    fileAppender.setAppend(true);
    fileAppender.setRollingPolicy(rollingPolicy);
    rollingPolicy.setParent(fileAppender);
    fileAppender.start();

    // Add to root logger
    Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.addAppender(fileAppender);
    if (!env.getConsoleLoggingEnabled().getValueOrDefault()) {
      LOG.info("Console logging is set to disabled. Set SP_LOGGING_CONSOLE_ENABLED to true to enable console logging.");
      rootLogger.detachAppender(CONSOLE_LOGGER);
    }

  }
}

