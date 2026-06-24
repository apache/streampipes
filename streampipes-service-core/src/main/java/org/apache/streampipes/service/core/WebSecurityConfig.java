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

package org.apache.streampipes.service.core;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.environment.model.OAuthConfiguration;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.service.base.security.UnauthorizedRequestEntryPoint;
import org.apache.streampipes.service.core.filter.TokenAuthenticationFilter;
import org.apache.streampipes.service.core.oauth2.CustomOAuth2UserService;
import org.apache.streampipes.service.core.oauth2.CustomOidcUserService;
import org.apache.streampipes.service.core.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import org.apache.streampipes.service.core.oauth2.OAuth2AccessTokenResponseConverterWithDefaults;
import org.apache.streampipes.service.core.oauth2.OAuth2AuthenticationFailureHandler;
import org.apache.streampipes.service.core.oauth2.OAuth2AuthenticationSuccessHandler;
import org.apache.streampipes.service.core.oauth2.OAuthEnabledCondition;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;
import org.apache.streampipes.user.management.service.SpUserDetailsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

  private static final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);

  private final UserDetailsService userDetailsService;
  private final StreamPipesPasswordEncoder passwordEncoder;
  private final Environment env;

  @Autowired
  private CustomOAuth2UserService customOAuth2UserService;

  @Autowired
  CustomOidcUserService customOidcUserService;

  @Autowired
  private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

  @Autowired
  private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

  private final SpResourceManager resourceManager;

  public WebSecurityConfig(StreamPipesPasswordEncoder passwordEncoder,
                           SpResourceManager resourceManager) {
    this.passwordEncoder = passwordEncoder;
    this.userDetailsService = new SpUserDetailsService(
        resourceManager.manageUsers().getDb(),
        resourceManager.managePermissions().getDb(),
        resourceManager.getRoleStorage(),
        resourceManager.getUserGroupStorage());
    this.env = Environments.getEnvironment();
    this.resourceManager = resourceManager;
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) {
    auth.userDetailsService(userDetailsService).passwordEncoder(this.passwordEncoder.passwordEncoder());
  }

  @Bean
  MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      PermissionEvaluator permissionEvaluator
  ) {
    var handler = new DefaultMethodSecurityExpressionHandler();
    handler.setPermissionEvaluator(permissionEvaluator);
    return handler;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
                                         ISpCoreConfigurationStorage coreConfigurationStorage) {
    http
        .cors(Customizer.withDefaults())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .exceptionHandling(eh -> eh.authenticationEntryPoint(new UnauthorizedRequestEntryPoint()))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(UnauthenticatedInterfaces.get().toArray(String[]::new)).permitAll()
            .anyRequest().authenticated()
        );
    if (env.getOAuthEnabled().getValueOrDefault()) {
      LOG.info("Configuring OAuth authentication from environment variables");
      http.oauth2Login(oauth -> oauth
          .authorizationEndpoint(ae -> ae
              .authorizationRequestRepository(cookieOAuth2AuthorizationRequestRepository())
          )
          .redirectionEndpoint(Customizer.withDefaults())
          .userInfoEndpoint(ui -> ui
              .oidcUserService(customOidcUserService)
              .userService(customOAuth2UserService)
          )
          .tokenEndpoint(te -> te
              .accessTokenResponseClient(authorizationCodeTokenResponseClient())
          )
          .successHandler(oAuth2AuthenticationSuccessHandler)
          .failureHandler(oAuth2AuthenticationFailureHandler)
      );
    }

    http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  public TokenAuthenticationFilter tokenAuthenticationFilter() {
    return new TokenAuthenticationFilter(resourceManager);
  }

  @Bean(BeanIds.USER_DETAILS_SERVICE)
  public UserDetailsService userDetailsService() {
    return userDetailsService;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public RequestAttributeSecurityContextRepository getRequestAttributeSecurityContextRepository() {
    return new RequestAttributeSecurityContextRepository();
  }

  @Bean
  @Conditional(OAuthEnabledCondition.class)
  public HttpCookieOAuth2AuthorizationRequestRepository cookieOAuth2AuthorizationRequestRepository() {
    return new HttpCookieOAuth2AuthorizationRequestRepository();
  }

  @Bean
  @Conditional(OAuthEnabledCondition.class)
  public ClientRegistrationRepository clientRegistrationRepository() {
    var registrations = getRegistrations();
    return new InMemoryClientRegistrationRepository(registrations);
  }

  private List<ClientRegistration> getRegistrations() {
    var oauthConfigs = Environments.getEnvironment().getOAuthConfigurations();

    return oauthConfigs.stream()
        .map(config -> {
          ClientRegistration.Builder builder = getBuilder(config);

          builder
              .registrationId(config.getRegistrationId())
              .clientId(config.getClientId())
              .clientSecret(config.getClientSecret())
              .clientName(config.getClientName())
              .scope(config.getScopes());

          if (StringUtils.hasText(config.getEmailAttributeName())) {
            builder.userNameAttributeName(config.getEmailAttributeName());
          }

          if (StringUtils.hasText(config.getAuthorizationUri())) {
            builder.authorizationUri(config.getAuthorizationUri());
          }
          if (StringUtils.hasText(config.getTokenUri())) {
            builder.tokenUri(config.getTokenUri());
          }
          if (StringUtils.hasText(config.getJwkSetUri())) {
            builder.jwkSetUri(config.getJwkSetUri());
          }
          if (StringUtils.hasText(config.getUserInfoUri())) {
            builder.userInfoUri(config.getUserInfoUri());
          }
          if (StringUtils.hasText(config.getIssuerUri())) {
            builder.issuerUri(config.getIssuerUri());
          }

          return builder.build();
        })
        .toList();
  }

  protected ClientRegistration.Builder getBuilder(OAuthConfiguration config) {
    ClientRegistration.Builder builder =
        StringUtils.hasText(config.getIssuerUri())
            ? ClientRegistrations.fromIssuerLocation(config.getIssuerUri())
            : ClientRegistration.withRegistrationId(config.getRegistrationId())
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);

    builder
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .redirectUri(String.format(
            "%s/streampipes-backend/{action}/oauth2/code/{registrationId}",
            env.getOAuthRedirectUri().getValueOrDefault()
        ));

    return builder;
  }

  private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeTokenResponseClient() {
    var tokenResponseConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
    tokenResponseConverter.setAccessTokenResponseConverter(
        new OAuth2AccessTokenResponseConverterWithDefaults()
    );

    RestClient restClient = RestClient.builder()
        .messageConverters(converters -> {
          converters.clear();
          converters.add(new FormHttpMessageConverter());
          converters.add(tokenResponseConverter);
        })
        .defaultStatusHandler(new OAuth2ErrorResponseErrorHandler())
        .build();

    var client = new RestClientAuthorizationCodeTokenResponseClient();
    client.setRestClient(restClient);

    return client;

  }
}
