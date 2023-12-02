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

import { ConnectUtils } from '../../support/utils/connect/ConnectUtils';
import { DashboardUtils } from '../../support/utils/DashboardUtils';

describe('Test All Widgets', () => {
    const dashboardName = 'testDashboard';

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ConnectUtils.addMachineDataSimulator('simulator', true);
        DashboardUtils.goToDashboard();
    });

    it('Perform Test', () => {
        DashboardUtils.addAndEditDashboard(dashboardName);

        // Test area chart
        DashboardUtils.testWidget('area', dashboardName, {
            minYaxis: '0',
            maxYaxis: '50',
        });

        // Test bar race chart
        DashboardUtils.testWidget('bar-race', dashboardName);

        // Test gauge widget
        DashboardUtils.testWidget('gauge', dashboardName, {
            minYaxis: '0',
            maxYaxis: '50',
        });

        // Test html widget
        DashboardUtils.testWidget('html', dashboardName);

        // Test line widget
        DashboardUtils.testWidget('line', dashboardName, {
            minYaxis: '0',
            maxYaxis: '50',
        });

        // Test raw widget
        DashboardUtils.testWidget('raw', dashboardName);

        // Test single value widget
        DashboardUtils.testWidget('number', dashboardName);

        // Test stacked line chart widget
        DashboardUtils.testWidget('stacked-line-chart', dashboardName);

        // Test status widget
        DashboardUtils.testWidget('status', dashboardName, {
            intervalKey: '5',
        });

        // Test table widget
        DashboardUtils.testWidget('table', dashboardName);

        // Test traffic light widget
        DashboardUtils.testWidget('trafficlight', dashboardName, {
            warningRange: '5',
            criticalValue: '5',
        });
    });
});
