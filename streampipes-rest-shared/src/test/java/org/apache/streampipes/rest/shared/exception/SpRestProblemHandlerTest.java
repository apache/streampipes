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

package org.apache.streampipes.rest.shared.exception;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.monitoring.SpLogMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.http.converter.json.ProblemDetailJacksonMixin;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The error contract: whatever a resource throws, the client receives an RFC 9457 problem detail
 * with the status that describes the failure and a {@code type} URN it can branch on.
 */
class SpRestProblemHandlerTest {

  private static final String PROBLEM_JSON = "application/problem+json";

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    // Spring Boot registers the ProblemDetail mixin on the application's JsonMapper; mirror that here.
    var mapper = JsonMapper.builder()
        .addMixIn(ProblemDetail.class, ProblemDetailJacksonMixin.class)
        .build();
    mockMvc = MockMvcBuilders.standaloneSetup(new FailingController())
        .setControllerAdvice(new SpRestProblemHandler())
        .setMessageConverters(new JacksonJsonHttpMessageConverter(mapper))
        .build();
  }

  @Test
  void problemExceptionBecomesProblemDetailWithTypeAndErrors() throws Exception {
    mockMvc.perform(get("/test/validation").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
        .andExpect(jsonPath("$.type").value("urn:streampipes:problem:validation-failed"))
        .andExpect(jsonPath("$.title").value("Validation failed"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.detail").value("2 fields are invalid"))
        .andExpect(jsonPath("$.instance").value("/test/validation"))
        .andExpect(jsonPath("$.errors[0].detail").value("host must not be empty"))
        .andExpect(jsonPath("$.errors[0].pointer").value("/configuration/0/host"))
        .andExpect(jsonPath("$.errors[1].detail").value("port must be a number"))
        .andExpect(jsonPath("$.errors[1].pointer").doesNotExist())
        .andExpect(jsonPath("$.notifications").doesNotExist())
        .andExpect(jsonPath("$.success").doesNotExist());
  }

  @Test
  void notFoundAndForbiddenCarryTheirStatus() throws Exception {
    mockMvc.perform(get("/test/not-found"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.type").value("urn:streampipes:problem:not-found"))
        .andExpect(jsonPath("$.detail").value("Adapter with id a-1 not found"));

    mockMvc.perform(get("/test/forbidden"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.type").value("urn:streampipes:problem:forbidden"))
        .andExpect(jsonPath("$.title").value("Forbidden"));
  }

  @Test
  void customPropertiesAreFlattenedIntoTheProblem() throws Exception {
    mockMvc.perform(get("/test/conflict"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.type").value("urn:streampipes:problem:conflict"))
        .andExpect(jsonPath("$.pipelineIds[0]").value("p-1"))
        .andExpect(jsonPath("$.properties").doesNotExist());
  }

  @Test
  void logMessageExceptionsKeepTheLogMessageFields() throws Exception {
    mockMvc.perform(get("/test/adapter-exception"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.type").value("urn:streampipes:problem:operation-failed"))
        .andExpect(jsonPath("$.title").value("worker unreachable"))
        .andExpect(jsonPath("$.level").value("ERROR"))
        .andExpect(jsonPath("$.cause").value("worker unreachable"))
        .andExpect(jsonPath("$.fullStackTrace").isString());

    mockMvc.perform(get("/test/log-message"))
        .andExpect(status().isGatewayTimeout())
        .andExpect(jsonPath("$.type").value("urn:streampipes:problem:operation-failed"))
        .andExpect(jsonPath("$.title").value("timed out"))
        .andExpect(jsonPath("$.detail").value("the model did not answer"))
        .andExpect(jsonPath("$.level").value("WARN"));

    mockMvc.perform(get("/test/no-endpoints"))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.type").value("urn:streampipes:problem:service-unavailable"));
  }

  @Test
  @SuppressWarnings("deprecation")
  void legacyMessageExceptionBecomesProblemDetail() throws Exception {
    mockMvc.perform(get("/test/legacy-message"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.type").value("urn:streampipes:problem:bad-request"))
        .andExpect(jsonPath("$.detail").value("Wrong group id provided"))
        .andExpect(jsonPath("$.notifications").doesNotExist());

    mockMvc.perform(get("/test/legacy-bad-request"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("Could not find widget"));
  }

  @Test
  void runtimeExceptionsGetAStatusThatDescribesThem() throws Exception {
    mockMvc.perform(get("/test/illegal-argument"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detail").value("limit must be positive"));

    mockMvc.perform(get("/test/no-such-element"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.type").value("urn:streampipes:problem:not-found"));

    mockMvc.perform(get("/test/access-denied"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.type").value("urn:streampipes:problem:forbidden"));

    mockMvc.perform(get("/test/bad-credentials"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.type").value("urn:streampipes:problem:unauthorized"));

    mockMvc.perform(get("/test/unexpected"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.type").value("urn:streampipes:problem:internal-error"))
        .andExpect(jsonPath("$.title").value("Internal server error"))
        .andExpect(jsonPath("$.detail").value("boom"));
  }

  @Test
  void springsOwnProblemDetailsGetTheStreamPipesType() throws Exception {
    mockMvc.perform(get("/test/response-status"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.type").value("urn:streampipes:problem:not-found"))
        .andExpect(jsonPath("$.detail").value("Pipeline p-1 not found"))
        .andExpect(jsonPath("$.instance").value("/test/response-status"));

    mockMvc.perform(get("/test/not-found").param("x", "y").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @RestController
  @RequestMapping("/test")
  static class FailingController {

    @GetMapping("/validation")
    String validation() {
      throw SpProblemException.validationFailed("2 fields are invalid", List.of(
          ProblemError.of("host must not be empty", "/configuration/0/host"),
          ProblemError.of("port must be a number")));
    }

    @GetMapping("/not-found")
    String notFound() {
      throw SpProblemException.notFound("Adapter", "a-1");
    }

    @GetMapping("/forbidden")
    String forbidden() {
      throw SpProblemException.forbidden("Not allowed to read adapter a-1");
    }

    @GetMapping("/conflict")
    String conflict() {
      throw SpProblemException.conflict("Adapter is in use").withProperty("pipelineIds", List.of("p-1"));
    }

    @GetMapping("/adapter-exception")
    String adapterException() throws AdapterException {
      throw new AdapterException("worker unreachable");
    }

    @GetMapping("/log-message")
    String logMessage() {
      throw new SpLogMessageException(HttpStatus.GATEWAY_TIMEOUT,
          SpLogMessage.warn("timed out", "the model did not answer"));
    }

    @GetMapping("/no-endpoints")
    String noEndpoints() throws NoServiceEndpointsAvailableException {
      throw new NoServiceEndpointsAvailableException("no service for app id x");
    }

    @GetMapping("/legacy-message")
    @SuppressWarnings("deprecation")
    String legacyMessage() {
      throw new SpMessageException(HttpStatus.BAD_REQUEST, Notifications.error("Wrong group id provided"));
    }

    @GetMapping("/legacy-bad-request")
    @SuppressWarnings("deprecation")
    String legacyBadRequest() {
      throw new BadRequestException("Could not find widget");
    }

    @GetMapping("/illegal-argument")
    String illegalArgument() {
      throw new IllegalArgumentException("limit must be positive");
    }

    @GetMapping("/no-such-element")
    String noSuchElement() {
      throw new NoSuchElementException("No value present");
    }

    @GetMapping("/access-denied")
    String accessDenied() {
      throw new AccessDeniedException("Access Denied");
    }

    @GetMapping("/bad-credentials")
    String badCredentials() {
      throw new BadCredentialsException("Bad credentials");
    }

    @GetMapping("/unexpected")
    String unexpected() {
      throw new IllegalStateException("boom");
    }

    @GetMapping("/response-status")
    String responseStatus() {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pipeline p-1 not found");
    }
  }
}
