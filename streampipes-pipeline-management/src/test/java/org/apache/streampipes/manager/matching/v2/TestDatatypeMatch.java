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

package org.apache.streampipes.manager.matching.v2;

import org.apache.streampipes.model.client.matching.MatchingResultMessage;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.XSD;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TestDatatypeMatch {

  @ParameterizedTest
  @MethodSource("datatypeMatches")
  public void testDatatypeMatch(String offer, String requirement, boolean expected) {
    List<MatchingResultMessage> errorLog = new ArrayList<>();

    Assertions.assertEquals(expected, new DatatypeMatch().match(offer, requirement, errorLog));
    Assertions.assertEquals(expected ? 0 : 1, errorLog.size());
    if (!expected) {
      Assertions.assertEquals(requirement, errorLog.getFirst().getRequirementSubject());
      Assertions.assertFalse(errorLog.getFirst().isMatchingSuccessful());
    }
  }

  private static Stream<Arguments> datatypeMatches() {
    return Stream.of(
        Arguments.of(XSD.INTEGER.toString(), XSD.INTEGER.toString(), true),
        Arguments.of(XSD.STRING.toString(), XSD.STRING.toString(), true),
        Arguments.of("custom:type", "custom:type", true),
        Arguments.of(SO.NUMBER, SO.NUMBER, true),
        Arguments.of(XSD.INTEGER.toString(), SO.NUMBER, true),
        Arguments.of(XSD.LONG.toString(), SO.NUMBER, true),
        Arguments.of(XSD.DOUBLE.toString(), SO.NUMBER, true),
        Arguments.of(XSD.FLOAT.toString(), SO.NUMBER, true),
        Arguments.of(XSD.INTEGER.toString(), XSD.STRING.toString(), false),
        Arguments.of(XSD.STRING.toString(), SO.NUMBER, false),
        Arguments.of("custom:type", SO.NUMBER, false),
        Arguments.of(SO.NUMBER, XSD.INTEGER.toString(), false),
        Arguments.of(XSD.INTEGER.toString(), XSD.LONG.toString(), false),
        Arguments.of(null, null, true),
        Arguments.of(XSD.INTEGER.toString(), null, true),
        Arguments.of(null, XSD.STRING.toString(), false),
        Arguments.of(null, SO.NUMBER, false)
    );
  }

  @Test
  public void testRepeatedMismatchDoesNotDuplicateError() {
    List<MatchingResultMessage> errorLog = new ArrayList<>();
    var matcher = new DatatypeMatch();

    Assertions.assertFalse(matcher.match(XSD.STRING.toString(), SO.NUMBER, errorLog));
    Assertions.assertFalse(matcher.match(null, SO.NUMBER, errorLog));

    Assertions.assertEquals(1, errorLog.size());
    Assertions.assertEquals(SO.NUMBER, errorLog.getFirst().getRequirementSubject());
  }
}
