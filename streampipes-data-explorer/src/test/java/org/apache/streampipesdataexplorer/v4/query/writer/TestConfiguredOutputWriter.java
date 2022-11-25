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

package org.apache.streampipesdataexplorer.v4.query.writer;

import org.junit.Before;

import java.util.Arrays;
import java.util.List;

public abstract class TestConfiguredOutputWriter {

  protected List<List<Object>> rows;
  protected List<String> columns;

  @Before
  public void before() {
    this.rows = Arrays.asList(
        Arrays.asList("2022-11-16T05:54:37.051Z", "test", 1),
        Arrays.asList("2022-11-16T05:55:27.05Z", "test2", 2)
    );

    this.columns = Arrays.asList("time", "string", "number");
  }


}
