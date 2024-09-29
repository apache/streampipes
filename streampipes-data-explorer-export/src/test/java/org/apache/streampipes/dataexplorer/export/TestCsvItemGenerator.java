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
package org.apache.streampipes.dataexplorer.export;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.streampipes.dataexplorer.export.item.CsvItemGenerator;

import org.junit.jupiter.api.Test;

public class TestCsvItemGenerator extends TestItemGenerator {

  private static final String ExpectedComma = "1668578077051,test,1";
  private static final String ExpectedSemicolon = "1668578077051;test;1";

  @Test
  public void testCsvItemWriterCommaSeparated() {
    var writer = new CsvItemGenerator(",");

    String result = writer.createItem(row, columns);

    assertEquals(ExpectedComma, result);
  }

  @Test
  public void testCsvItemWriterSemicolonSeparated() {
    var writer = new CsvItemGenerator(";");

    String result = writer.createItem(row, columns);

    assertEquals(ExpectedSemicolon, result);
  }
}
