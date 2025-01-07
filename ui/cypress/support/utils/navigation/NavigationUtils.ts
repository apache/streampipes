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
    // Static variables for module names
    public static readonly PIPELINES = 'pipelines';
    public static readonly CONNECT = 'connect';
    public static readonly DASHBOARD = 'dashboard';
    public static readonly DATA_EXPLORER = 'dataexplorer';
    public static readonly ASSET_MANAGEMENT = 'assets';
    public static readonly CONFIGURATION = 'configuration';

    public static readonly ALL_MODULES = [
        NavigationUtils.PIPELINES,
        NavigationUtils.CONNECT,
        NavigationUtils.DASHBOARD,
        NavigationUtils.DATA_EXPLORER,
        NavigationUtils.ASSET_MANAGEMENT,
        NavigationUtils.CONFIGURATION,
    ];

    /**
     * Validates that only the specified navigation icons are displayed.
     * @param displayedModules List of module names that should be visible.
     */
    public static validateActiveModules(displayedModules: string[]) {
        NavigationUtils.ALL_MODULES.forEach(module => {
            const shouldBeDisplayed = displayedModules.includes(module);
            this.validateNavigationIcon(module, shouldBeDisplayed);
        });
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
