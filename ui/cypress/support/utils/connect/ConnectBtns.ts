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
export class ConnectBtns {
    public static infoAdapter() {
        return cy.dataCy('info-adapter');
    }

    public static editAdapter() {
        return cy.dataCy('edit-adapter');
    }

    public static stopAdapter() {
        return cy.dataCy('stop-adapter');
    }

    public static startAdapter() {
        return cy.dataCy('start-adapter');
    }

    public static refreshSchema() {
        return cy.dataCy('refresh-schema');
    }

    public static storeEditAdapter() {
        return cy.dataCy('store-edit-adapter');
    }

    // =====================  Adapter settings btns  ==========================
    public static adapterSettingsStartAdapter() {
        return cy.dataCy('adapter-settings-start-adapter-btn');
    }

    public static startAdapterNowCheckbox() {
        return cy.dataCy('start-adapter-now-checkbox');
    }

    public static startAllAdapters() {
        return cy.dataCy('start-all-adapters-btn');
    }

    public static stopAllAdapters() {
        return cy.dataCy('stop-all-adapters-btn');
    }

    // ========================================================================

    // =====================  Format configurations  ==========================

    public static csvDelimiter() {
        return 'undefined-org.apache.streampipes.extensions.management.connect.adapter.parser.csv-1-delimiter-0';
    }

    public static csvHeader() {
        return 'undefined-org.apache.streampipes.extensions.management.connect.adapter.parser.csv-1-header-1';
    }

    public static jsonArrayFieldKey() {
        return 'undefined-arrayFieldConfig-2-key-0';
    }

    public static xmlTag() {
        return 'undefined-org.apache.streampipes.extensions.management.connect.adapter.parser.xml-2-tag-0';
    }

    // ========================================================================
}
