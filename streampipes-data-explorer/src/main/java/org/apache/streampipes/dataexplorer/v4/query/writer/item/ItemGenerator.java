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


package org.apache.streampipes.dataexplorer.v4.query.writer.item;

import org.apache.streampipes.dataexplorer.v4.utils.TimeParser;

import java.util.List;
import java.util.StringJoiner;

public abstract class ItemGenerator {

  protected static final String COMMA_SEPARATOR = ",";

  private final String separator;

  public ItemGenerator(String separator) {
    this.separator = separator;
  }

  public String createItem(List<Object> row,
                           List<String> columns) {
    StringJoiner joiner = new StringJoiner(separator);

    for (int i = 0; i < row.size(); i++) {
      var value = row.get(i);
      if (i == 0) {
        value = TimeParser.parseTime(value.toString());
      }
      joiner.add(makeItemString(columns.get(i), value));
    }

    return finalizeItem(joiner.toString());
  }

  protected abstract String makeItemString(String key,
                                           Object value);

  protected abstract String finalizeItem(String item);

}
