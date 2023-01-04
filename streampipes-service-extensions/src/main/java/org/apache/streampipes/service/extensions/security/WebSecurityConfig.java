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

package org.apache.streampipes.service.extensions.security;

import org.apache.streampipes.commons.constants.Envs;
import org.apache.streampipes.service.base.security.UnauthorizedRequestEntryPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

  private static final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);

  private final UserDetailsService userDetailsService;

  public WebSecurityConfig() {
    this.userDetailsService = username -> null;
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    if (isAnonymousAccess()) {
      http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          .and()
          .csrf().disable()
          .formLogin().disable()
          .httpBasic().disable().authorizeHttpRequests().requestMatchers("/**").permitAll();
    } else {
      http
          .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          .and()
          .csrf().disable()
          .formLogin().disable()
          .httpBasic().disable()
          .exceptionHandling()
          .authenticationEntryPoint(new UnauthorizedRequestEntryPoint())
          .and()
          .authorizeHttpRequests((authz) -> authz
              .requestMatchers(UnauthenticatedInterfaces
                  .get()
                  .stream()
                  .map(AntPathRequestMatcher::new)
                  .toList()
                  .toArray(new AntPathRequestMatcher[0]))
              .permitAll()
              .anyRequest().authenticated()
              .and()
              .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class));
    }

    return http.build();
  }

  private boolean isAnonymousAccess() {
    if (Envs.SP_EXT_AUTH_MODE.exists() && Envs.SP_EXT_AUTH_MODE.getValue().equals("AUTH")) {
      if (Envs.SP_JWT_PUBLIC_KEY_LOC.exists()) {
        LOG.info("Configured service for authenticated access mode");
        return false;
      } else {
        LOG.warn(
            "No env variable {} provided, which is required for authenticated access. Defaulting to anonymous access.",
            Envs.SP_JWT_PUBLIC_KEY_LOC.getEnvVariableName());
        return true;
      }
    } else {
      LOG.info("Configured anonymous access for this service, consider providing an authentication option.");
      return true;
    }
  }

  public TokenAuthenticationFilter tokenAuthenticationFilter() {
    return new TokenAuthenticationFilter();
  }

  @Bean(BeanIds.USER_DETAILS_SERVICE)
  public UserDetailsService userDetailsService() {
    return userDetailsService;
  }

}
