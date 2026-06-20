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

package org.apache.streampipes.connect.transformer.groovy.sandbox.error;

import org.apache.streampipes.connect.transformer.groovy.sandbox.SandboxPolicy;

import java.util.Optional;

public final class SandboxCompilationMessageResolver {

  private SandboxCompilationMessageResolver() {
  }

  public static String resolve(Exception error) {
    Optional<SandboxViolationException> sandboxViolation = findSandboxViolation(error);
    if (sandboxViolation.isPresent()) {
      return sandboxViolation.get().getMessage();
    }

    return extractViolationMessage(error.getMessage())
        .or(() -> extractAllowedError(error.getMessage()))
        .orElse("Failed to compile Groovy template");
  }

  private static Optional<SandboxViolationException> findSandboxViolation(Throwable error) {
    Throwable current = error;
    while (current != null) {
      if (current instanceof SandboxViolationException sandboxViolation) {
        return Optional.of(sandboxViolation);
      } else if (current instanceof SandboxViolationClassNotFoundException sandboxViolation) {
        return Optional.of(new SandboxViolationException(sandboxViolation.getMessage()));
      } else if (current instanceof SecurityException securityException) {
        return toSandboxViolation(securityException.getMessage());
      }
      current = current.getCause();
    }

    return Optional.empty();
  }

  private static Optional<SandboxViolationException> toSandboxViolation(String message) {
    if (message == null || message.isBlank()) {
      return Optional.empty();
    }

    if (message.contains(SandboxPolicy.VIOLATION_MESSAGE)) {
      return Optional.of(new SandboxViolationException(message));
    }

    if (message.contains("is not allowed")) {
      return Optional.of(new SandboxViolationException(SandboxPolicy.VIOLATION_MESSAGE + ": " + message));
    }

    return Optional.empty();
  }

  private static Optional<String> extractViolationMessage(String message) {
    if (message == null) {
      return Optional.empty();
    }

    int messageIndex = message.indexOf(SandboxPolicy.VIOLATION_MESSAGE);
    if (messageIndex < 0) {
      return Optional.empty();
    }

    int lineBreakIndex = message.indexOf('\n', messageIndex);
    if (lineBreakIndex < 0) {
      return Optional.of(message.substring(messageIndex).trim());
    }

    return Optional.of(message.substring(messageIndex, lineBreakIndex).trim());
  }

  private static Optional<String> extractAllowedError(String message) {
    if (message == null) {
      return Optional.empty();
    }

    int messageIndex = message.indexOf("is not allowed");
    if (messageIndex < 0) {
      return Optional.empty();
    }

    int lineStartIndex = message.lastIndexOf('\n', messageIndex);
    int start = lineStartIndex < 0 ? 0 : lineStartIndex + 1;
    int lineBreakIndex = message.indexOf('\n', messageIndex);
    String extracted = (lineBreakIndex < 0 ? message.substring(start) : message.substring(start, lineBreakIndex)).trim();

    if (extracted.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(SandboxPolicy.VIOLATION_MESSAGE + ": " + extracted);
  }
}
