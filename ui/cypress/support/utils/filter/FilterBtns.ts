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

export class FilterBtns {
    public static filterBtn() {
        return cy.dataCy('filter-button', { timeout: 10000 });
    }

    public static resetFiltersBtn() {
        return cy.dataCy('reset-filters-btn', { timeout: 10000 });
    }

    public static filtersDeselectAssets() {
        return cy.dataCy('filters-deselect-Assets', { timeout: 10000 });
    }

    public static filtersAssetSelect() {
        return cy.dataCy('filters-asset-select', { timeout: 10000 });
    }

    public static filtersDeselectSites() {
        return cy.dataCy('filters-deselect-Sites', { timeout: 10000 });
    }

    public static filtersSitesSelect() {
        return cy.dataCy('filters-sites-select', { timeout: 10000 });
    }

    public static filtersDeselectLabels() {
        return cy.dataCy('filters-deselect-Labels', { timeout: 10000 });
    }

    public static filtersLabelsSelect() {
        return cy.dataCy('filters-labels-select', { timeout: 10000 });
    }

    public static filtersDeselectTypes() {
        return cy.dataCy('filters-deselect-Type', { timeout: 10000 });
    }

    public static filtersTypesSelect() {
        return cy.dataCy('filters-types-select', { timeout: 10000 });
    }

    public static applyFiltersBtn() {
        return cy.dataCy('apply-filters-btn', { timeout: 10000 });
    }
}
