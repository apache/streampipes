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
export class NavigationUtils {
    /**
     * Validates that the Pipelines navigation icon is displayed.
     */
    public static pipelinesIsDisplayed() {
        this.validateNavigationIcon('pipelines', true);
    }

    /**
     * Validates that the Pipelines navigation icon is not displayed.
     */
    public static pipelinesNotDisplayed() {
        this.validateNavigationIcon('pipelines', false);
    }

    /**
     * Validates that the Connect navigation icon is displayed.
     */
    public static connectIsDisplayed() {
        this.validateNavigationIcon('connect', true);
    }

    /**
     * Validates that the Connect navigation icon is not displayed.
     */
    public static connectNotDisplayed() {
        this.validateNavigationIcon('connect', false);
    }

    /**
     * Validates that the Dashboard navigation icon is displayed.
     */
    public static dashboardIsDisplayed() {
        this.validateNavigationIcon('dashboard', true);
    }

    /**
     * Validates that the Dashboard navigation icon is not displayed.
     */
    public static dashboardNotDisplayed() {
        this.validateNavigationIcon('dashboard', false);
    }

    /**
     * Validates that the Data Explorer navigation icon is displayed.
     */
    public static dataExplorerIsDisplayed() {
        this.validateNavigationIcon('dataexplorer', true);
    }

    /**
     * Validates that the Data Explorer navigation icon is not displayed.
     */
    public static dataExplorerNotDisplayed() {
        this.validateNavigationIcon('dataexplorer', false);
    }

    /**
     * Validates that the Asset Management navigation icon is displayed.
     */
    public static assetManagementIsDisplayed() {
        this.validateNavigationIcon('assets', true);
    }

    /**
     * Validates that the Asset Management navigation icon is not displayed.
     */
    public static assetManagementNotDisplayed() {
        this.validateNavigationIcon('assets', false);
    }

    /**
     * Validates that the Configuration navigation icon is displayed.
     */
    public static configurationIsDisplayed() {
        this.validateNavigationIcon('configuration', true);
    }

    /**
     * Validates that the Configuration navigation icon is not displayed.
     */
    public static configurationNotDisplayed() {
        this.validateNavigationIcon('configuration', false);
    }

    /**
     * Validates the visibility of a navigation icon.
     * @param icon The icon identifier.
     * @param shown Whether the icon should be shown.
     */
    private static validateNavigationIcon(icon: string, shown: boolean) {
        const expectedLength = shown ? 1 : 0;
        cy.dataCy(`navigation-icon-${icon}`, { timeout: 10000 }).should(
            'have.length',
            expectedLength,
        );
    }
}
