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
package org.apache.streampipes.dataexplorer.v4;

import java.util.Arrays;
import java.util.List;

public class SupportedDataLakeQueryParameters {

  public static final String QP_COLUMNS = "columns";
  public static final String QP_START_DATE = "startDate";
  public static final String QP_END_DATE = "endDate";
  public static final String QP_PAGE = "page";
  public static final String QP_LIMIT = "limit";
  public static final String QP_OFFSET = "offset";
  public static final String QP_GROUP_BY = "groupBy";
  public static final String QP_ORDER = "order";
  public static final String QP_AGGREGATION_FUNCTION = "aggregationFunction";
  public static final String QP_TIME_INTERVAL = "timeInterval";
  public static final String QP_FORMAT = "format";
  public static final String QP_CSV_DELIMITER = "delimiter";

  public static final String QP_MISSING_VALUE_BEHAVIOUR = "missingValueBehaviour";
  public static final String QP_COUNT_ONLY = "countOnly";
  public static final String QP_AUTO_AGGREGATE = "autoAggregate";
  public static final String QP_FILTER = "filter";
  public static final String QP_MAXIMUM_AMOUNT_OF_EVENTS = "maximumAmountOfEvents";

  public static final List<String> SUPPORTED_PARAMS = Arrays.asList(
      QP_COLUMNS,
      QP_START_DATE,
      QP_END_DATE,
      QP_PAGE,
      QP_LIMIT,
      QP_OFFSET,
      QP_GROUP_BY,
      QP_ORDER,
      QP_AGGREGATION_FUNCTION,
      QP_TIME_INTERVAL,
      QP_FORMAT,
      QP_CSV_DELIMITER,
      QP_COUNT_ONLY,
      QP_AUTO_AGGREGATE,
      QP_MISSING_VALUE_BEHAVIOUR,
      QP_FILTER,
      QP_MAXIMUM_AMOUNT_OF_EVENTS
  );

}
