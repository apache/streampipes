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

import { SiteUtils } from '../../../support/utils/configuration/SiteUtils';
import { ConfigurationUtils } from '../../../support/utils/configuration/ConfigurationUtils';

describe('Test configuration of sites', () => {
    beforeEach('Setup Test', () => {
        cy.initStreamPipesTest();
    });

    it('Perform Test', () => {
        const site = 'My Site';
        const newSite = 'My modified Site';
        const areas = ['Area A', 'Area B'];
        ConfigurationUtils.goToSitesConfiguration();
        SiteUtils.createNewSite(site, areas);

        cy.dataCy(SiteUtils.LABEL_TABLE_NAME).should('have.length', 1);

        cy.dataCy(SiteUtils.LABEL_TABLE_NAME)
            .first()
            .should('contain.text', site);
        cy.dataCy(SiteUtils.LABEL_TABLE_AREA)
            .first()
            .should('contains.text', areas.toString());

        SiteUtils.openEditSiteDialog();

        cy.dataCy(SiteUtils.INPUT_SITE_DIALOG_SITE_INPUT, { timeout: 2000 })
            .clear()
            .type(newSite);

        cy.dataCy(SiteUtils.BUTTON_SITE_DIALOG_REMOVE_AREA + '_Area_A').click();
        cy.dataCy(SiteUtils.BUTTON_SITE_DIALOG_SAVE).click();

        cy.dataCy(SiteUtils.LABEL_TABLE_NAME).should('have.length', 1);

        cy.dataCy(SiteUtils.LABEL_TABLE_NAME)
            .first()
            .should('contain.text', newSite);
        cy.dataCy(SiteUtils.LABEL_TABLE_AREA)
            .first()
            .should('have.text', ' Area B ');

        cy.dataCy(SiteUtils.BUTTON_DELETE_SITE + '-My_modified_Site').click();
        cy.dataCy(SiteUtils.LABEL_TABLE_NAME).should('have.length', 0);
    });
});
