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

package org.apache.streampipes.dataexplorer.influx;

import org.apache.streampipes.dataexplorer.api.query.QueryTimestampFormat;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import org.influxdb.dto.QueryResult;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/** Preserves fine-precision epoch integers while retaining the existing double-valued field contract. */
final class InfluxQueryResultDecoder {
  private InfluxQueryResultDecoder() {
  }

  static JsonAdapter<QueryResult> create(QueryTimestampFormat format) {
    if (format != QueryTimestampFormat.EPOCH_NANOS && format != QueryTimestampFormat.EPOCH_MICROS) {
      return new Moshi.Builder().build().adapter(QueryResult.class);
    }
    var delegate = new Moshi.Builder().add(Object.class, new ExactNumbers()).build().adapter(QueryResult.class);
    return new JsonAdapter<>() {
      @Override
      public QueryResult fromJson(JsonReader reader) throws IOException {
        var result = delegate.fromJson(reader);
        if (result != null && result.getResults() != null) {
          for (var statement : result.getResults()) {
            if (statement.getSeries() == null) {
              continue;
            }
            for (var series : statement.getSeries()) {
              if (series.getValues() == null) {
                continue;
              }
              int time = series.getColumns().indexOf("time");
              for (var row : series.getValues()) {
                for (int i = 0; i < row.size(); i++) {
                  if (row.get(i) instanceof BigDecimal number) {
                    // Avoid conditional numeric promotion, which would turn Long into Double again.
                    if (i == time) {
                      row.set(i, number.longValueExact());
                    } else {
                      row.set(i, number.doubleValue());
                    }
                  }
                }
              }
            }
          }
        }
        return result;
      }

      @Override
      public void toJson(JsonWriter writer, QueryResult value) throws IOException {
        throw new UnsupportedOperationException("Query decoder is read-only");
      }
    };
  }

  private static final class ExactNumbers extends JsonAdapter<Object> {
    @Override
    public Object fromJson(JsonReader reader) throws IOException {
      return switch (reader.peek()) {
        case NUMBER -> new BigDecimal(reader.nextString());
        case STRING -> reader.nextString();
        case BOOLEAN -> reader.nextBoolean();
        case NULL -> reader.nextNull();
        case BEGIN_ARRAY -> {
          var values = new ArrayList<Object>();
          reader.beginArray();
          while (reader.hasNext()) {
            values.add(fromJson(reader));
          }
          reader.endArray();
          yield values;
        }
        case BEGIN_OBJECT -> {
          var values = new LinkedHashMap<String, Object>();
          reader.beginObject();
          while (reader.hasNext()) {
            values.put(reader.nextName(), fromJson(reader));
          }
          reader.endObject();
          yield values;
        }
        default -> throw new IOException("Unexpected query value: " + reader.peek());
      };
    }

    @Override
    public void toJson(JsonWriter writer, Object value) throws IOException {
      throw new UnsupportedOperationException("Query decoder is read-only");
    }
  }
}
