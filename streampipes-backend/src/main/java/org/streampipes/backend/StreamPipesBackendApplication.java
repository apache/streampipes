/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.backend;

import io.swagger.v3.jaxrs2.integration.OpenApiServlet;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.servlet.OncePerRequestFilter;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.streampipes.app.file.export.application.AppFileExportApplication;
import org.streampipes.rest.notifications.NotificationListener;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

@SpringBootApplication
public class StreamPipesBackendApplication {

  private static final Logger LOG = LoggerFactory.getLogger(StreamPipesBackendApplication.class.getCanonicalName());

  public static void main(String[] args) {
    SpringApplication.run(StreamPipesBackendApplication.class, args);
  }

  @PreDestroy
  public void onExit() {
   LOG.info("Shutting down StreamPipes...");
    //Operations.stopAllPipelines();
  }

  @Bean
  public ServletRegistrationBean appFileExportRegistrationBean() {
    ServletContainer jerseyContainer = new ServletContainer(new AppFileExportApplication());
    return new ServletRegistrationBean<>(jerseyContainer, "/api/apps/*");
  }

  @Bean
  public ServletRegistrationBean swaggerRegistrationBean() {
    ServletRegistrationBean<HttpServlet> bean = new ServletRegistrationBean<>(new OpenApiServlet()
            , "/api/docs/*");
    Map<String, String> params = new HashMap<>();
    params.put("openApi.configuration.resourcePackages", "io.swagger.sample.resource");
    bean.setInitParameters(params);
    return bean;
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
