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
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.vocabulary.Geo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestSchemaMatch {

  @Test
  public void testPositiveSchemaMatch() {

    EventPropertyPrimitive offer1 = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.LAT);
    EventPropertyPrimitive offer2 = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.LNG);

    EventPropertyPrimitive requirement1 = EpRequirements.integerReq();
    EventPropertyPrimitive requirement2 = EpRequirements.integerReq();

    EventSchema offeredSchema = new EventSchema(Arrays.asList(offer1, offer2));
    EventSchema requiredSchema = new EventSchema(Arrays.asList(requirement1, requirement2));

    List<MatchingResultMessage> errorLog = new ArrayList<>();

    boolean matches = new SchemaMatch().match(offeredSchema, requiredSchema, errorLog);
    assertTrue(matches);
  }

  @Test
  public void testNegativeSchemaMatch() {

    EventPropertyPrimitive offer1 = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.LAT);
    EventPropertyPrimitive offer2 = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.LNG);

    EventPropertyPrimitive requirement1 = EpRequirements.integerReq();
    EventPropertyPrimitive requirement2 = EpRequirements.stringReq();

    EventSchema offeredSchema = new EventSchema(Arrays.asList(offer1, offer2));
    EventSchema requiredSchema = new EventSchema(Arrays.asList(requirement1, requirement2));

    List<MatchingResultMessage> errorLog = new ArrayList<>();

    boolean matches = new SchemaMatch().match(offeredSchema, requiredSchema, errorLog);
    assertFalse(matches);
  }

  @Test
  public void testNegativeSchemaMatchDomain() {

    EventPropertyPrimitive offer1 = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.LAT);
    EventPropertyPrimitive offer2 = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.LNG);

    EventPropertyPrimitive requirement1 = EpRequirements.domainPropertyReq(Geo.LAT);
    EventPropertyPrimitive requirement2 = EpRequirements.stringReq();

    EventSchema offeredSchema = new EventSchema(Arrays.asList(offer1, offer2));
    EventSchema requiredSchema = new EventSchema(Arrays.asList(requirement1, requirement2));

    List<MatchingResultMessage> errorLog = new ArrayList<>();

    boolean matches = new SchemaMatch().match(offeredSchema, requiredSchema, errorLog);
    assertFalse(matches);
  }

  @Test
  public void testPositiveSchemaMatchDomain() {

    EventPropertyPrimitive offer1 = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.LAT);
    EventPropertyPrimitive offer2 = EpProperties.integerEp(Labels.empty(), "timestamp", Geo.LNG);

    EventPropertyPrimitive requirement1 = EpRequirements.domainPropertyReq(Geo.LAT);
    EventPropertyPrimitive requirement2 = EpRequirements.integerReq();

    EventSchema offeredSchema = new EventSchema(Arrays.asList(offer1, offer2));
    EventSchema requiredSchema = new EventSchema(Arrays.asList(requirement1, requirement2));

    List<MatchingResultMessage> errorLog = new ArrayList<>();

    boolean matches = new SchemaMatch().match(offeredSchema, requiredSchema, errorLog);
    assertTrue(matches);
  }
}
