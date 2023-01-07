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

import { CanNotEditAdapterDialog } from './can-not-edit-adapter-dialog.component';
import { DialogRef } from '@streampipes/shared-ui';
import { Pipeline } from '@streampipes/platform-services';

describe('CanNotEditAdapterDialog', () => {
    it('Show error message and containing pipelines', () => {
        const expectedPipelineName = 'Demo pipeline';
        const pipeline = new Pipeline();
        pipeline.name = expectedPipelineName;

        mount(
            `<sp-can-not-edit-adapter-dialog
                                                   [pipelines]="pipelines">
                   </sp-can-not-edit-adapter-dialog>`,
            { pipelines: [pipeline] },
        );

        cy.dataCy('can-not-edit-adapter-dialog-warning').should('be.visible');
        cy.dataCy('adapter-in-pipeline').should('have.length', 1);
        cy.dataCy('adapter-in-pipeline').contains(expectedPipelineName);
    });

    const mount = (template: string, cp?) => {
        cy.mount(template, {
            declarations: [CanNotEditAdapterDialog],
            providers: [{ provide: DialogRef, useValue: {} }],
            componentProperties: cp,
        });
    };
});
