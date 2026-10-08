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

import { ConfigurationBtns } from '../../../support/utils/configuration/ConfigurationBtns';
import { ConfigurationUtils } from '../../../support/utils/configuration/ConfigurationUtils';

describe('Show a system notification to all users', () => {
    const message = 'Maintenance tonight from 8 pm';

    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
        ConfigurationUtils.enableSystemNotification(message);
    });

    afterEach('Switch the notification off again', () => {
        // The reset does not touch the general configuration
        cy.login();
        ConfigurationUtils.disableSystemNotification();
    });

    it('Shows the notification in the toolbar and on the login page', () => {
        ConfigurationBtns.systemNotification().should('contain.text', message);
        cy.logout();
        ConfigurationBtns.systemNotification().should('contain.text', message);
    });

    it('Hides the notification once it is switched off', () => {
        ConfigurationUtils.disableSystemNotification();
        ConfigurationBtns.systemNotification().should('not.exist');
    });
});
