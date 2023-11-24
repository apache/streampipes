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

export class DashboardUtils {
    public static goToDashboard() {
        cy.visit('#/dashboard');
    }

    public static showDashboard(dashboardName: string) {
        cy.dataCy('show-dashboard-' + dashboardName).click();
    }

    public static showStandaloneDashboard(dashboardName: string) {
        cy.dataCy('show-dashboard-' + dashboardName).click();
        cy.location('href').then(url => {
            const dashboardId = url.substring(url.lastIndexOf('/') + 1);
            cy.visit(`#/standalone/${dashboardId}`);
            cy.wait(2000);
        });
    }

    public static addAndEditDashboard(dashboardName: string) {
        cy.dataCy('new-dashboard-btn').click();
        cy.dataCy('dashboard-name-input').type(dashboardName);
        cy.dataCy('dashboard-save-btn').click();

        // Start editing dashboard dashboard
        cy.dataCy('edit-dashboard-' + dashboardName).click();
    }

    public static addWidget(
        pipelineName: string,
        widgetType: string,
        options?,
    ) {
        // Add raw data widget
        cy.dataCy('dashboard-add-widget').click();

        // Select Pipeline to visualize
        cy.dataCy('dashboard-visualize-pipeline-' + pipelineName).click();

        // Select widget
        cy.dataCy('dashboard-select-widget-' + widgetType).click();

        if (widgetType == 'area' || widgetType == 'line') {
            cy.dataCy('min-y-axis-key').type(options.minYaxis);
            cy.dataCy('max-y-axis-key').type(options.maxYaxis);
        } else if (widgetType == 'gauge') {
            cy.dataCy('min-key').type(options.minYaxis);
            cy.dataCy('max-key').type(options.maxYaxis);
        } else if (widgetType == 'status') {
            cy.dataCy('interval-key').type(options.intervalKey);
        } else if (widgetType == 'trafficlight') {
            cy.dataCy('critical-value-key').type(options.criticalValue);
            cy.dataCy('warning-range').type(options.warningRange);
        }

        // optional configure widget
        cy.dataCy('dashboard-new-widget-next-btn').click();

        // Finish edit mode
        cy.dataCy('dashboard-save-edit-mode').click();

        cy.wait(1000);
    }

    public static validateRawWidgetEvents(amountOfEvents: number) {
        cy.dataCy('dashboard-raw-item', { timeout: 10000 })
            .its('length')
            .should('be.gte', amountOfEvents);
    }

    public static removeWidgetFromDashboard(dashboardName: string) {
        cy.visit('#/dashboard');
        cy.dataCy('edit-dashboard-' + dashboardName).click();
        cy.dataCy('widget-remove-button').click();
    }

    public static testWidget(
        widgetType: string,
        dashboardName: string,
        options?: {
            minYaxis?: string;
            maxYaxis?: string;
            intervalKey?: string;
            criticalValue?: string;
            warningRange?: string;
        },
    ) {
        DashboardUtils.addWidget('Persist_simulator', widgetType, options);
        if (widgetType == 'status') {
            cy.get(`sp-dashboard-${widgetType.toLowerCase()}-widget`).should(
                'be.visible',
            );
        } else if (widgetType == 'trafficlight') {
            cy.get(`sp-traffic-light-widget`).should('be.visible');
        } else {
            cy.get(`sp-${widgetType.toLowerCase()}-widget`).should(
                'be.visible',
            );
        }

        DashboardUtils.removeWidgetFromDashboard(dashboardName);
    }
}
