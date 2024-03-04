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

package org.apache.streampipes.extensions.connectors.opcua.migration;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.connectors.opcua.migration.config.OpcUaAdapterVersionedConfig;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.vocabulary.XSD;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class OpcUaAdapterMigrationV2Test {

  private OpcUaAdapterMigrationV2 migrationV2;

  @Before
  public void setUp() {
    migrationV2 = new OpcUaAdapterMigrationV2();
  }

  @Test
  public void testOPCUAAdapterMigrationV2() {
    var adapterDescriptionV1 = OpcUaAdapterVersionedConfig.getOpcUaAdapterDescriptionV1();
    var extractorMock = mock(IStaticPropertyExtractor.class);

    var adapterDescriptionV2 = migrationV2.migrate(adapterDescriptionV1, extractorMock)
                                          .element();

    assertEquals(XSD.INTEGER, getTypeOfPortProperty(adapterDescriptionV2));
  }

  private URI getTypeOfPortProperty(AdapterDescription adapterDescriptionV2) {
    return migrationV2
        .extractPortProperty(adapterDescriptionV2)
        .getRequiredDatatype();
  }
}