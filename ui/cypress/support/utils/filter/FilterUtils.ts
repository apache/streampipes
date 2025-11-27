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

import { FilterBtns } from './FilterBtns';

export class FilterUtils {
    public static clearFilter() {
        FilterBtns.filterBtn().click();
        FilterBtns.resetFiltersBtn().click();
    }

    public static selectAssetFilter(name: string) {
        cy.dataCy(`asset-option-${name}`).click();
    }

    public static selectSiteFilter(name: string) {
        cy.dataCy(`site-option-${name}`).click();
    }

    public static selectLabelFilter(name: string) {
        cy.dataCy(`label-option-${name}`).click();
    }

    public static selectTypeFilter(name: string) {
        cy.dataCy(`type-option-${name}`).click();
    }

    public static filterAssets(assetNames: string[]) {
        FilterBtns.filterBtn().click();
        FilterBtns.filtersDeselectAssets().click();
        FilterBtns.filtersAssetSelect().click();
        assetNames.forEach(assetName => {
            this.selectAssetFilter(assetName);
        });

        // click somewhere else to close the dropdown
        cy.get('body').click(0, 0);
        FilterBtns.applyFiltersBtn().click();
    }

    public static filterSites(siteNames: string[]) {
        FilterBtns.filterBtn().click();
        FilterBtns.filtersDeselectSites().click();
        FilterBtns.filtersSitesSelect().click();

        siteNames.forEach(siteName => {
            this.selectSiteFilter(siteName);
        });

        // click somewhere else to close the dropdown
        cy.get('body').click(0, 0);
        FilterBtns.applyFiltersBtn().click();
    }

    public static filterLabels(siteNames: string[]) {
        FilterBtns.filterBtn().click();
        FilterBtns.filtersDeselectLabels().click();
        FilterBtns.filtersLabelsSelect().click();

        siteNames.forEach(siteName => {
            this.selectLabelFilter(siteName);
        });

        // click somewhere else to close the dropdown
        cy.get('body').click(0, 0);
        FilterBtns.applyFiltersBtn().click();
    }

    public static filterTypes(typeNames: string[]) {
        FilterBtns.filterBtn().click();
        FilterBtns.filtersDeselectTypes().click();
        FilterBtns.filtersTypesSelect().click();

        typeNames.forEach(siteName => {
            this.selectTypeFilter(siteName);
        });

        // click somewhere else to close the dropdown
        cy.get('body').click(0, 0);
        FilterBtns.applyFiltersBtn().click();
    }
}
