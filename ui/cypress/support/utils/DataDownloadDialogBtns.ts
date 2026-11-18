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

export class DataDownloadDialogBtns {
    public static nextBtn() {
        return cy.dataCy('download-configuration-next-btn', { timeout: 10000 });
    }

    public static downloadBtn() {
        return cy.dataCy('download-configuration-download-btn', {
            timeout: 10000,
        });
    }

    public static selectedDatasets() {
        return cy.dataCy('download-selected-datasets', { timeout: 10000 });
    }

    public static fileSummary() {
        return cy.dataCy('download-file-summary', { timeout: 30000 });
    }

    public static fileErrors() {
        return cy.dataCy('download-file-errors');
    }
}
