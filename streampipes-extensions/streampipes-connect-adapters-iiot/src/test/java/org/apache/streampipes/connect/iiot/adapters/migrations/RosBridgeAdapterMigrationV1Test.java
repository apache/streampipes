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

package org.apache.streampipes.connect.iiot.adapters.migrations;

import org.apache.streampipes.connect.iiot.adapters.migrations.config.RosBridgeAdapterVersionedConfig;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.vocabulary.XSD;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


public class RosBridgeAdapterMigrationV1Test {

  private RosBridgeAdapterMigrationV1 migrationV1;

  @Before
  public void setUp() {
    migrationV1 = new RosBridgeAdapterMigrationV1();
  }

  @Test
  public void testMigrationV1(){
    var rosBridgeAdapterDescriptionV0 = RosBridgeAdapterVersionedConfig.getRosBridgeAdapterDescriptionV0();
    var extractorMock = mock(IStaticPropertyExtractor.class);

    var rosBridgeAdapterDescriptionV1 = migrationV1.migrate(rosBridgeAdapterDescriptionV0 , extractorMock);

    var typeOfPortProperty = getTypeOfPortProperty(rosBridgeAdapterDescriptionV1.element());
    assertEquals(XSD.INTEGER, typeOfPortProperty);
  }

  private URI getTypeOfPortProperty(AdapterDescription adapterDescription) {
    return migrationV1
        .extractPortProperty(adapterDescription)
        .getRequiredDatatype();
  }

}