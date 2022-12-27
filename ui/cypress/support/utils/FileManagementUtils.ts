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

export class FileManagementUtils {
    public static addFile(filePath: string) {
        // Go to StreamPipes file management
        cy.visit('#/files');

        // Open file upload dialog
        cy.dataCy('sp-open-file-upload-dialog').click();

        // Upload file
        // const filepath = 'fileTest/test.csv'
        cy.dataCy('sp-file-management-file-input').attachFile(filePath);
        cy.dataCy('sp-file-management-store-file').click();
    }

    public static deleteFile() {
        // Go to StreamPipes file management
        cy.visit('#/files');
        // Check if file was uploaded and delete it
        cy.dataCy('delete').should('have.length', 1);
        cy.dataCy('delete').click();
        cy.dataCy('confirm-delete').click();
        cy.dataCy('delete').should('have.length', 0);
    }
}
