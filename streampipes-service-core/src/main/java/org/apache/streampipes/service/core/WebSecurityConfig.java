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
import org.apache.streampipes.service.base.security.UnauthorizedRequestEntryPoint;
import org.apache.streampipes.service.core.filter.TokenAuthenticationFilter;
import org.apache.streampipes.service.core.oauth2.CustomOAuth2UserService;
import org.apache.streampipes.service.core.oauth2.CustomOidcUserService;
import org.apache.streampipes.service.core.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import org.apache.streampipes.service.core.oauth2.OAuth2AccessTokenResponseConverterWithDefaults;
import org.apache.streampipes.service.core.oauth2.OAuth2AuthenticationFailureHandler;
import org.apache.streampipes.service.core.oauth2.OAuth2AuthenticationSuccessHandler;
import org.apache.streampipes.service.core.oauth2.OAuthEnabledCondition;
import org.apache.streampipes.user.management.service.SpUserDetailsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
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

  public WebSecurityConfig(StreamPipesPasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
    this.userDetailsService = new SpUserDetailsService();
    this.env = Environments.getEnvironment();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(this.passwordEncoder.passwordEncoder());
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors()
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .csrf().disable()
        .formLogin().disable()
        .httpBasic().disable()
        .exceptionHandling()
        .authenticationEntryPoint(new UnauthorizedRequestEntryPoint())
        .and()
        .authorizeHttpRequests((authz) -> {
          try {
            authz
                .requestMatchers(UnauthenticatedInterfaces
                    .get()
                    .stream()
                    .map(AntPathRequestMatcher::new)
                    .toList()
                    .toArray(new AntPathRequestMatcher[0]))
                .permitAll()
                .anyRequest()
                .authenticated();

            if (env.getOAuthEnabled().getValueOrDefault()) {
              LOG.info("Configuring OAuth authentication from environment variables");
              authz
                  .and()
                  .oauth2Login()
                  .authorizationEndpoint()
                  .authorizationRequestRepository(cookieOAuth2AuthorizationRequestRepository())
                  .and()
                  .redirectionEndpoint()
                  .and()
                  .userInfoEndpoint()
                  .oidcUserService(customOidcUserService)
                  .userService(customOAuth2UserService)
                  .and()
                  .tokenEndpoint()
                  .accessTokenResponseClient(authorizationCodeTokenResponseClient())
                  .and()
                  .successHandler(oAuth2AuthenticationSuccessHandler)
                  .failureHandler(oAuth2AuthenticationFailureHandler);
            }
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });


    http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  public TokenAuthenticationFilter tokenAuthenticationFilter() {
    return new TokenAuthenticationFilter();
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

    return oauthConfigs.stream().map(config -> {
          ClientRegistration.Builder builder = this.getBuilder(config.getRegistrationId());
          builder.scope(config.getScopes());
          builder.authorizationUri(config.getAuthorizationUri());
          builder.tokenUri(config.getTokenUri());
          builder.jwkSetUri(config.getJwkSetUri());
          builder.issuerUri(config.getIssuerUri());
          builder.userInfoUri(config.getUserInfoUri());
          builder.clientSecret(config.getClientSecret());
          builder.userNameAttributeName(config.getEmailAttributeName());
          builder.clientName(config.getClientName());
          builder.clientId(config.getClientId());
          return builder.build();
        }
    ).toList();
  }

  protected final ClientRegistration.Builder getBuilder(String registrationId) {
    ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId);
    builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
    builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
    builder.redirectUri(
        String.format("%s/streampipes-backend/{action}/oauth2/code/{registrationId}",
            env.getOAuthRedirectUri().getValueOrDefault()
        )
    );
    return builder;
  }

  private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeTokenResponseClient() {
    var tokenResponseHttpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
    tokenResponseHttpMessageConverter
        .setAccessTokenResponseConverter(new OAuth2AccessTokenResponseConverterWithDefaults());
    var restTemplate = new RestTemplate(
        List.of(new FormHttpMessageConverter(), tokenResponseHttpMessageConverter)
    );
    restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
    var tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
    tokenResponseClient.setRestOperations(restTemplate);
    return tokenResponseClient;

  }

}
