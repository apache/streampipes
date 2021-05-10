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
package org.apache.streampipes.backend;

import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.servlet.OncePerRequestFilter;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.node.management.NodeManagement;
import org.apache.streampipes.rest.notifications.NotificationListener;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContextListener;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAutoConfiguration
@Import({StreamPipesResourceConfig.class, WelcomePageController.class})
public class StreamPipesBackendApplication {

  private static final Logger LOG = LoggerFactory.getLogger(StreamPipesBackendApplication.class.getCanonicalName());

  public static void main(String[] args) {
    System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
    SpringApplication.run(StreamPipesBackendApplication.class, args);
  }

  @PostConstruct
  public void init() {
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    executorService.schedule(this::startAllPreviouslyStoppedPipelines, 5, TimeUnit.SECONDS);

    LOG.info("Starting StreamPipes node management ...");
    NodeManagement.getInstance().init();
  }

  @PreDestroy
  public void onExit() {
    LOG.info("Shutting down StreamPipes...");
    LOG.info("Flagging currently running pipelines for restart...");
    getAllPipelines()
            .stream()
            .filter(Pipeline::isRunning)
            .forEach(pipeline -> {
              pipeline.setRestartOnSystemReboot(true);
              StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().updatePipeline(pipeline);
            });

    LOG.info("Gracefully stopping all running pipelines...");
    List<PipelineOperationStatus> status = Operations.stopAllPipelines();
    status.forEach(s -> {
      if (s.isSuccess()) {
        LOG.info("Pipeline {} successfully stopped", s.getPipelineName());
      } else {
        LOG.error("Pipeline {} could not be stopped", s.getPipelineName());
      }
    });

    LOG.info("Thanks for using Apache StreamPipes - see you next time!");
  }

  private void startAllPreviouslyStoppedPipelines() {
    LOG.info("Checking for orphaned pipelines...");
    getAllPipelines()
            .stream()
            .filter(Pipeline::isRunning)
            .forEach(pipeline -> {
              LOG.info("Restoring orphaned pipeline {}", pipeline.getName());
              startPipeline(pipeline);
            });

    LOG.info("Checking for gracefully shut down pipelines to be restarted...");

    getAllPipelines()
            .stream()
            .filter(p -> ! (p.isRunning()))
            .filter(Pipeline::isRestartOnSystemReboot)
            .forEach(pipeline -> {
              startPipeline(pipeline);
              pipeline.setRestartOnSystemReboot(false);
              StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().updatePipeline(pipeline);
            });

    LOG.info("No more pipelines to restore...");
  }

  private void startPipeline(Pipeline pipeline) {
    PipelineOperationStatus status = Operations.startPipeline(pipeline);
    if (status.isSuccess()) {
      LOG.info("Pipeline {} successfully restarted", status.getPipelineName());
    } else {
      LOG.error("Pipeline {} could not be restarted - are all pipeline element containers running?", status.getPipelineName());
    }
  }

  private List<Pipeline> getAllPipelines() {
    return StorageDispatcher.INSTANCE
            .getNoSqlStore()
            .getPipelineStorageAPI()
            .getAllPipelines();
  }

  @Bean
  public FilterRegistrationBean shiroFilterBean() {
    FilterRegistrationBean<OncePerRequestFilter> bean = new FilterRegistrationBean<>();
    bean.setFilter(new ShiroFilter());
    bean.addUrlPatterns("/api/*");
    return bean;
  }

  @Bean
  public ServletListenerRegistrationBean shiroListenerBean() {
    return listener(new EnvironmentLoaderListener());
  }

  @Bean
  public ServletListenerRegistrationBean streamPipesNotificationListenerBean() {
    return listener(new NotificationListener());
  }

  private ServletListenerRegistrationBean listener(ServletContextListener listener) {
    ServletListenerRegistrationBean<ServletContextListener> bean =
            new ServletListenerRegistrationBean<>();
    bean.setListener(listener);
    return bean;
  }

}
