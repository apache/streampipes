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

package org.apache.streampipes.connect.shared.preprocessing.transform.value;

import org.apache.streampipes.connect.shared.preprocessing.SupportsNestedTransformationRule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class TimestampTransformationRule extends SupportsNestedTransformationRule {

  private final List<String> eventKey;
  private final TimestampTranformationRuleMode mode;
  private final long multiplier;

  private SimpleDateFormat dateFormatter;

  private static final Logger logger = LoggerFactory.getLogger(TimestampTransformationRule.class);

  public TimestampTransformationRule(List<String> eventKey,
                                     TimestampTranformationRuleMode mode,
                                     String formatString,
                                     long multiplier) {
    this.eventKey = eventKey;
    this.mode = mode;
    this.multiplier = multiplier;

    if (mode == TimestampTranformationRuleMode.FORMAT_STRING) {
      dateFormatter = new SimpleDateFormat(formatString);
    }
  }

  @Override
  protected List<String> getEventKeys() {
    return eventKey;
  }

  @Override
  protected void applyTransformation(Map<String, Object> event, List<String> eventKey) {
    switch (mode) {
      case TIME_UNIT -> {
        long timeLong = Long.parseLong(String.valueOf(event.get(eventKey.get(0))));
        event.put(eventKey.get(0), this.performTimeUnitTransformation(timeLong));
      }
      case FORMAT_STRING -> {
        String dateString = String.valueOf(event.get(eventKey.get(0)));
        event.put(eventKey.get(0), performFormatStringTransformation(dateString));
      }
    }

  }

  private long performTimeUnitTransformation(long time) {
    return time * multiplier;
  }

  private long performFormatStringTransformation(String date) {
    //TODO how to handle exception?
    try {
      return dateFormatter.parse(date).getTime();
    } catch (ParseException e) {
      logger.error(e.toString());
    }
    return 0;
  }
}
