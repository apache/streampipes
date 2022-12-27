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

import { OutputStrategyConfig } from '../model/OutputStrategyConfig';

export class OutputStrategyUtils {
    public static input(outputStrategyConfig: OutputStrategyConfig) {
        if (outputStrategyConfig !== undefined) {
            if (outputStrategyConfig.type === 'append') {
                cy.dataCy('use-input-schema').click();
            }

            if (
                outputStrategyConfig.config &&
                outputStrategyConfig.config.length > 0
            ) {
                outputStrategyConfig.config.forEach(config => {
                    cy.dataCy('add-field').click();

                    // Set runtime name
                    cy.dataCy('runtime-name').last().type(config.runtimeName);

                    // select property type
                    cy.dataCy('runtime-type')
                        .last()
                        .click()
                        .get('mat-option')
                        .contains(config.runtimeType)
                        .click();

                    // Set semantic type
                    if (config.semanticType) {
                        cy.dataCy('semantic-type')
                            .last()
                            .type(config.semanticType);
                    }
                });
            }
        }
    }
}
