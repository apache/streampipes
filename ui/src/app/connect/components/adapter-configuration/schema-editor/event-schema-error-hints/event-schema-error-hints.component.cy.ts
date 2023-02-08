/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import { EventSchemaErrorHintsComponent } from './event-schema-error-hints.component';
import { UserErrorMessage } from '../../../../../core-model/base/UserErrorMessage';

describe('EventSchemaErrorHints', () => {
    it('schema ok', () => {
        mount(`<sp-event-schema-error-hints
                                                 [isError]="false"
                                                 [isLoading]="false"
                                                 [schemaErrorHints]=[]>
                   </sp-event-schema-error-hints>`);

        cy.dataCy('schema-validation-ok').should('be.visible');
        cy.get('.schema-validation-text-ok').contains('Schema ok');
        cy.get('.material-icons').contains('check_circle');
    });

    it('loading is true', () => {
        mount(`<sp-event-schema-error-hints
                                                [isError]="false"
                                                [isLoading]="true"
                                                [schemaErrorHints]=[]>
                   </sp-event-schema-error-hints>`);

        cy.dataCy('schema-validation-ok').should('not.exist');
        cy.dataCy('schema-validation-error').should('not.exist');
    });

    it('error is true', () => {
        mount(`<sp-event-schema-error-hints
                                                [isError]="true"
                                                [isLoading]="false"
                                                [schemaErrorHints]=[]>
                   </sp-event-schema-error-hints>`);

        cy.dataCy('schema-validation-ok').should('not.exist');
        cy.dataCy('schema-validation-error').should('not.exist');
    });

    const sampleHint = new UserErrorMessage(
        'sample error',
        'This is a test error',
    );

    it('display one error', () => {
        mount(
            `<sp-event-schema-error-hints
                                                [isError]="false"
                                                [isLoading]="false"
                                                [schemaErrorHints]="schemaErrorHints">
                   </sp-event-schema-error-hints>`,
            { schemaErrorHints: [sampleHint] },
        );

        cy.dataCy('schema-validation-ok').should('not.exist');
        cy.dataCy('schema-validation-error')
            .should('be.visible')
            .should('have.length', 1);
        cy.get('.schema-validation-text-error').contains(sampleHint.title);
        cy.dataCy('schema-validation-error-content').contains(
            sampleHint.content,
        );
        cy.get('.material-icons').contains('warning');
    });

    it('display multiple errors', () => {
        mount(
            `<sp-event-schema-error-hints
                                                [isError]="false"
                                                [isLoading]="false"
                                                [schemaErrorHints]="schemaErrorHints">
                   </sp-event-schema-error-hints>`,
            { schemaErrorHints: [sampleHint, sampleHint, sampleHint] },
        );

        cy.dataCy('schema-validation-ok').should('not.exist');
        cy.dataCy('schema-validation-error')
            .should('be.visible')
            .should('have.length', 3);
    });

    const mount = (template: string, cp?) => {
        cy.mount(template, {
            declarations: [EventSchemaErrorHintsComponent],
            componentProperties: cp,
        });
    };
});
