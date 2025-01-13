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

export class ConfigurationUtils {
    public static goToConfigurationExport() {
        cy.visit('#/configuration/export');
    }

    public static goToSitesConfiguration() {
        cy.visit('#/configuration/sites');
    }

    public static goToGeneralConfiguration() {
        cy.visit('#/configuration/general');
    }

    public static goToLabelConfiguration() {
        cy.visit('#/configuration/labels');
    }

    public static addNewLabel(name: string, description: string) {
        cy.dataCy('new-label-button').click();
        cy.dataCy('label-name').type(name);
        cy.dataCy('label-description').type(description);
        cy.dataCy('save-label-button').click();
    }

    public static checkLabel(labelName: string) {
        cy.dataCy('available-labels-list').should('have.length', 1);
        cy.dataCy('label-text').should($el => {
            const text = $el.text().trim();
            expect(text).to.equal(labelName);
        });
    }

    public static deleteLabel() {
        cy.dataCy('delete-label-button').click();
        cy.dataCy('available-labels-list').should('have.length', 0);
    }
}
