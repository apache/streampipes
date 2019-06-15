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
package org.streampipes.connect.adapter.specific.simulator;

import static org.streampipes.sdk.helpers.EpProperties.*;

import org.streampipes.model.connect.guess.GuessSchema;
import org.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.vocabulary.SO;

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
