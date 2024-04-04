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

export class PipelineElementTemplateUtils {
    public static addTemplate(templateName: string) {
        cy.dataCy('use-template').should('not.exist');
        cy.dataCy('create-template').click();
        cy.dataCy('template-name').type(templateName);
        cy.dataCy('template-description').type(templateName);
        cy.dataCy('save-template').click();
    }

    public static selectTemplate(templateName: string) {
        cy.dataCy('use-template').should('exist');
        cy.dataCy('use-template').click({ force: true });
        cy.get('mat-option').contains(templateName).click({ force: true });
    }

    public static deleteTemplate() {
        cy.dataCy('create-template').click();
        cy.dataCy('delete-pipeline-element-template').should('have.length', 1);
        cy.dataCy('delete-pipeline-element-template').click();
        cy.dataCy('delete-pipeline-element-template').should('have.length', 0);
    }
}
