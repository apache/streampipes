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

import { UserInput } from '../model/UserInput';

export class StaticPropertyUtils {
    public static input(configs: UserInput[]) {
        // Configure Properties
        configs.forEach(config => {
            if (config.type === 'checkbox') {
                cy.dataCy(config.selector).children().click();
            } else if (config.type === 'button') {
                cy.dataCy(config.selector).click();
            } else if (config.type === 'drop-down') {
                cy.dataCy(config.selector)
                    .click()
                    .get('mat-option')
                    .contains(config.value)
                    .click();
            } else if (config.type === 'radio') {
                cy.dataCy(
                    config.selector.replace(' ', '_').toLowerCase() +
                        '-' +
                        config.value.replace(' ', '_').toLowerCase(),
                ).click();
            } else if (config.type === 'click') {
                cy.dataCy(config.selector).click({ force: true });
            } else if (config.type === 'code-input') {
                cy.dataCy('reset-code-' + config.selector).click();
                cy.dataCy('code-editor-' + config.selector).type(config.value);
            } else if (config.type === 'input') {
                cy.dataCy(config.selector).clear().type(config.value).blur();
            } else if (config.type === 'slider') {
                cy.dataCy(config.selector).type(config.value);
            } else {
                cy.dataCy(config.selector).type(config.value);
            }
        });
    }
}
