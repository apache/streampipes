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

export class SiteUtils {
    public static CHECKBOX_ENABLE_LOCATION_FEATURES =
        'sites-enable-location-features-checkbox';
    public static INPUT_TILE_SERVER_URL = 'sites-location-config-tile-server';

    public static BUTTON_MANAGE_SITES = 'sites-manage-sites-button';
    public static BUTTON_EDIT_SITE = 'sites-edit-button';
    public static BUTTON_DELETE_SITE = 'sites-delete-button';
    public static BUTTON_SITE_DIALOG_SAVE = 'sites-dialog-save-button';
    public static BUTTON_SITE_DIALOG_CANCEL = 'sites-dialog-cancel-button';
    public static BUTTON_SITE_DIALOG_REMOVE_AREA =
        'sites-dialog-remove-area-button';
    public static BUTTON_SITE_DIALOG_ADD_AREA = 'sites-dialog-add-area-button';

    public static INPUT_SITE_DIALOG_SITE_INPUT = 'sites-dialog-site-input';
    public static INPUT_SITE_DIALOG_NEW_AREA_INPUT =
        'sites-dialog-new-area-input';

    public static LABEL_TABLE_NAME = 'site-table-row-label';
    public static LABEL_TABLE_AREA = 'site-table-row-areas';

    public static enableGeoFeatures(tileServerUrl: string): void {
        cy.dataCy(SiteUtils.CHECKBOX_ENABLE_LOCATION_FEATURES)
            .children()
            .click();
        cy.dataCy(SiteUtils.INPUT_TILE_SERVER_URL, { timeout: 1000 })
            .clear()
            .type(tileServerUrl);
        cy.dataCy('sites-location-features-button').click();
    }

    public static createNewSite(name: string, areas: string[]): void {
        cy.dataCy(SiteUtils.BUTTON_MANAGE_SITES).click();
        cy.dataCy(SiteUtils.INPUT_SITE_DIALOG_SITE_INPUT, { timeout: 2000 })
            .clear()
            .type(name);

        areas.forEach(area => {
            cy.dataCy(SiteUtils.INPUT_SITE_DIALOG_NEW_AREA_INPUT)
                .clear()
                .type(area);
            cy.dataCy(SiteUtils.BUTTON_SITE_DIALOG_ADD_AREA).click();
        });

        cy.dataCy(this.BUTTON_SITE_DIALOG_SAVE).click();
    }

    public static openEditSiteDialog() {
        cy.dataCy(SiteUtils.BUTTON_EDIT_SITE).first().click();
    }
}
