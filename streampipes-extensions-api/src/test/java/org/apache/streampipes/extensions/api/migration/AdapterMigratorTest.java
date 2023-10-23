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

package org.apache.streampipes.extensions.api.migration;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AdapterMigratorTest {

  @Test
  public void migrationOrdering(){
    var migratorA1 = new MigratorTestClass<AdapterMigrator>("app-id", 1, 2);
    var migratorA2 = new MigratorTestClass<AdapterMigrator>("app-id", 2, 3);
    var migratorB = new MigratorTestClass<AdapterMigrator>("other-id", 1, 2);

    List<MigratorTestClass<AdapterMigrator>> migrators = new ArrayList<>(List.of(migratorA2, migratorA1, migratorB));
    Collections.sort(migrators);

    assertEquals(List.of(migratorA1, migratorA2, migratorB), migrators);
  }
}
