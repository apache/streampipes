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
package org.apache.streampipes.connect.adapters.simulator.random;

import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.vocabulary.SO;

import static org.apache.streampipes.sdk.helpers.EpProperties.*;

public class RandomDataSimulatorUtils {

  private static final String TIMESTAMP = "timestamp";
  private static final String RANDOM_NUMBER = "randomNumber";
  private static final String RANDOM_TEXT = "randomText";
  private static final String COUNT = "count";

  public static GuessSchema randomSchema() {
    return GuessSchemaBuilder.create()
            .property(timestampProperty(TIMESTAMP))
            .property(integerEp(Labels.from(RANDOM_NUMBER, "Random Number", "A random number"),
                    RANDOM_NUMBER, SO.Number))
            .property(stringEp(Labels.from(RANDOM_TEXT, "Random Text", "A random text value"),
                    RANDOM_TEXT,
                    SO.Text))
            .property(integerEp(Labels.from(COUNT, "count", "Count value"),
                    COUNT, SO.Number))
            .build();
  }
}
