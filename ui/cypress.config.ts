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

// tslint:disable-next-line:no-implicit-dependencies
import { defineConfig } from 'cypress';

export default defineConfig({
    projectId: 'q1jdu2',
    downloadsFolder: 'cypress/downloads',

    env: {
        TAKE_SCREENSHOT: 'false',
    },

    retries: {
        runMode: 1,
        openMode: 0,
    },

    trashAssetsBeforeRuns: true,
    videoCompression: false,
    viewportWidth: 1920,
    viewportHeight: 1080,

    e2e: {
        // We've imported your old cypress plugins here.
        // You may want to clean this up later by importing these.
        setupNodeEvents(on, config) {
            return require('./cypress/plugins/index.ts')(on, config);
        },
        specPattern: 'cypress/tests/**/*.{js,jsx,ts,tsx}',
        baseUrl: 'http://localhost:80',
    },

    component: {
        devServer: {
            framework: 'angular',
            bundler: 'webpack',
        },
        setupNodeEvents(on, config) {
            return require('./cypress/plugins/index.ts')(on, config);
        },
        specPattern: '**/*.cy.ts',
    },
});
