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

declare global {
    namespace Cypress {
        interface Chainable {
            dataCy: typeof dataCy;
        }
    }
}

/**
 * Selects elements based on the `data-cy` attribute.
 *
 * @param {string} value - The value of the `data-cy` attribute to match.
 * @param {object} [config={}] - Optional configuration object for the Cypress `get` command.
 * @param {boolean} [startsWith=false] - If true, selects elements whose `data-cy` attribute starts with the given value.
 * @returns {Cypress.Chainable<JQuery<HTMLElement>>} - A chainable Cypress object containing the matched elements.
 */
export const dataCy = (
    value: string,
    config: any = {},
    startsWith: boolean = false,
) => {
    const selector = startsWith ? `[data-cy^=${value}]` : `[data-cy=${value}]`;
    return cy.get(selector, config);
};
