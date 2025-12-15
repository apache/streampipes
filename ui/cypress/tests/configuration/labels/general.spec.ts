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

import { ConfigurationUtils } from '../../../support/utils/configuration/ConfigurationUtils';
import { DashboardUtils } from '../../../support/utils/DashboardUtils';
import { ConfigurationBtns } from '../../../support/utils/configuration/ConfigurationBtns';

describe('Change basic settings', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {
        ConfigurationUtils.goToGeneralConfiguration();
        const appName = 'TEST APP';
        const hostname = 'testHost';
        const port = '123';

        // Rename app, change localhost and port
        ConfigurationBtns.generalConfigAppNameInput().clear().type(appName);
        ConfigurationBtns.generalConfigHostnameInput().clear().type(hostname);
        ConfigurationBtns.generalConfigPortInput().clear().type(port);
        ConfigurationBtns.generalConfigSaveBtn().click();

        // Leave, Re-visit configuration and check values
        DashboardUtils.goToDashboard();

        ConfigurationUtils.goToGeneralConfiguration();
        ConfigurationBtns.generalConfigAppNameInput().should(
            'have.value',
            appName,
        );
        ConfigurationBtns.generalConfigHostnameInput().should(
            'have.value',
            hostname,
        );
        ConfigurationBtns.generalConfigPortInput().should('have.value', port);
    });
});
