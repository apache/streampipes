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
package org.apache.streampipes.dataexplorer.autoagg;

import org.apache.streampipes.dataexplorer.model.Order;
import org.apache.streampipes.dataexplorer.param.QueryParams;
import org.apache.streampipes.dataexplorer.query.GetDateFromSortedTableRecord;

import java.util.function.Supplier;

public abstract class AbstractAutoAggregationQuery<Q extends QueryParams, OUT> {

  private static final double NUM_OF_AUTO_AGGREGATION_VALUES = 2000;

  protected Q params;
  private Supplier<OUT> supplier;

  public AbstractAutoAggregationQuery(Q params, Supplier<OUT> supplier) {
    this.params = params;
    this.supplier = supplier;
  }

  public OUT executeQuery() {
    double numberOfRecords = getCount();

    if (numberOfRecords == 0) {
      return supplier.get();
    } else if (numberOfRecords <= NUM_OF_AUTO_AGGREGATION_VALUES) {
      return getRawEvents();
    } else {
      int aggregationValue = getAggregationValue(params.getIndex());
      return getAggregatedEvents(aggregationValue);
    }
  }

  protected abstract double getCount();

  protected abstract OUT getRawEvents();

  protected abstract OUT getAggregatedEvents(Integer aggregationValue);

  private int getAggregationValue(String index) {
    long timerange = getDateFromNewestRecordOfTable(index) - getDateFromOldestRecordOfTable(index);
    double v = timerange / NUM_OF_AUTO_AGGREGATION_VALUES;
    return Double.valueOf(v).intValue();
  }

  private long getDateFromNewestRecordOfTable(String index) {
    return new GetDateFromSortedTableRecord(QueryParams.from(index), Order.DESC).executeQuery();
  }

  private long getDateFromOldestRecordOfTable(String index) {
    return new GetDateFromSortedTableRecord(QueryParams.from(index), Order.ASC).executeQuery();
  }
}
