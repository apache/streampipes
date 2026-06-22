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
 */

import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { DatasetBtns } from '../../support/utils/dataset/DatasetBtns';
import { DatasetUtils } from '../../support/utils/dataset/DatasetUtils';

describe('Test Data Set Details', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Shows schema and latest events and opens chart creation', () => {
        const adapterName = 'Machine Data Simulator Dataset Details';

        ConnectUtils.addMachineDataSimulator(adapterName, true, '100');
        DatasetUtils.openDatasetDetails(adapterName);

        DatasetBtns.datasetDetailsSchemaTable().should('be.visible');
        DatasetUtils.expectSchemaField('density', 'Number');
        DatasetUtils.expectSchemaField('mass_flow', 'Number');
        DatasetUtils.expectSchemaField('sensorId', 'Text');
        DatasetUtils.expectSchemaField('sensor_fault_flags', 'Boolean');

        DatasetUtils.openLatestEventsTab();
        DatasetUtils.expectLatestEventsForColumn('density');
        DatasetUtils.expectLatestEventsForColumn('mass_flow');
        DatasetUtils.setLatestEventsLimit(5);
        DatasetUtils.expectLatestEventsForColumn('density');
        DatasetUtils.expectLatestEventsForColumn('mass_flow');

        DatasetUtils.createChartFromDatasetDetails();

        cy.url().should('include', '#/chart/create');
        cy.url().should(
            'include',
            `measureName=${encodeURIComponent(adapterName)}`,
        );
        cy.dataCy('data-explorer-select-data-set').should(
            'have.value',
            adapterName,
        );
    });
});
