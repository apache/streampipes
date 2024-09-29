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
package org.apache.streampipes.processors.transformation.jvm.processor.staticmetadata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.streampipes.sdk.utils.Datatypes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StaticMetaDataEnrichmentProcessorTest {

  private StaticMetaDataEnrichmentProcessor processor;

  @BeforeEach
  public void setup() {
    processor = new StaticMetaDataEnrichmentProcessor();
  }

  @Test
  public void transformToStreamPipesDataTypeForValidOption() {
    assertEquals(Datatypes.Boolean,
            processor.transformToStreamPipesDataType(StaticMetaDataEnrichmentProcessor.OPTION_BOOL));
    assertEquals(Datatypes.String,
            processor.transformToStreamPipesDataType(StaticMetaDataEnrichmentProcessor.OPTION_STRING));
    assertEquals(Datatypes.Float,
            processor.transformToStreamPipesDataType(StaticMetaDataEnrichmentProcessor.OPTION_FLOAT));
    assertEquals(Datatypes.Integer,
            processor.transformToStreamPipesDataType(StaticMetaDataEnrichmentProcessor.OPTION_INTEGER));
  }

  @Test
  public void transformToStreamPipesDataTypeForInvalidOption() {
    assertThrows(IllegalArgumentException.class, () -> processor.transformToStreamPipesDataType("InvalidOption"));
  }

  @Test
  public void castValueOfMetaDataConfigurationBoolean() {
    var config = getSampleMetaDataConfiguration("true", StaticMetaDataEnrichmentProcessor.OPTION_BOOL);

    assertEquals(true, processor.castValueOfMetaDataConfiguration(config));
  }

  @Test
  public void castValueOfMetaDataConfigurationFloat() {
    var config = getSampleMetaDataConfiguration("1.23", StaticMetaDataEnrichmentProcessor.OPTION_FLOAT);

    assertEquals(1.23f, processor.castValueOfMetaDataConfiguration(config));
  }

  @Test
  public void castValueOfMetaDataConfigurationInteger() {
    var config = getSampleMetaDataConfiguration("123", StaticMetaDataEnrichmentProcessor.OPTION_INTEGER);

    assertEquals(123, processor.castValueOfMetaDataConfiguration(config));
  }

  @Test
  public void castValueOfMetaDataConfigurationString() {
    var config = getSampleMetaDataConfiguration("test", StaticMetaDataEnrichmentProcessor.OPTION_STRING);

    assertEquals("test", processor.castValueOfMetaDataConfiguration(config));
  }

  @Test
  public void castValueOfMetaDataConfigurationDefault() {
    StaticMetaDataConfiguration config = new StaticMetaDataConfiguration("runtimeName", "default", "default");

    assertEquals("default", processor.castValueOfMetaDataConfiguration(config));
  }

  private static StaticMetaDataConfiguration getSampleMetaDataConfiguration(String value, String dataType) {
    return new StaticMetaDataConfiguration("runtimeName", value, dataType);
  }

}
