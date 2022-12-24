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

// ***********************************************************
// This example plugins/index.js can be used to load plugins
//
// You can change the location of this file or turn off loading
// the plugins file with the 'pluginsFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/plugins-guide
// ***********************************************************
// This function is called when a project is opened or re-opened (e.g. due to
// the project's config changing)
import * as fs from 'fs';
import { ProcessorTest } from '../support/model/ProcessorTest';

// tslint:disable-next-line:no-var-requires
const { rmdir } = require('fs');

// tslint:disable-next-line:no-var-requires no-implicit-dependencies
const webpackPreprocessor = require('@cypress/webpack-batteries-included-preprocessor');

function readProcessingElements(): ProcessorTest[] {
    const result: ProcessorTest[] = [];

    const allPipelineTests = fs.readdirSync('cypress/fixtures/pipelineElement');

    allPipelineTests.forEach(dir => {
        const subfolder = fs.readdirSync(
            'cypress/fixtures/pipelineElement/' + dir,
        );
        subfolder.forEach(test => {
            const testDescription = fs.readFileSync(
                'cypress/fixtures/pipelineElement/' +
                    dir +
                    '/' +
                    test +
                    '/description.json',
            );
            // @ts-ignore
            const pt = new ProcessorTest();
            pt.name = test;
            pt.dir = dir + '/' + test;

            const configDir = fs.readdirSync(
                'cypress/fixtures/pipelineElement/' + pt.dir,
            );
            configDir.forEach(file => {
                if (file.startsWith('input')) {
                    pt.inputFile = file;
                }
            });

            // @ts-ignore
            pt.processor = JSON.parse(testDescription);

            result.push(pt);
        });
    });

    return result;
}

module.exports = (on, config) => {
    const webpackOptions = webpackPreprocessor.defaultOptions.webpackOptions;

    webpackOptions.resolve.fallback = {
        stream: require.resolve('stream-browserify'),
    };
    webpackOptions.module.rules.unshift({
        test: /[/\\]@angular[/\\].+\.m?js$/,
        resolve: {
            fullySpecified: false,
        },
        use: {
            loader: 'babel-loader',
            options: {
                plugins: ['@angular/compiler-cli/linker/babel'],
                compact: false,
                cacheDirectory: true,
            },
        },
    });

    on(
        'file:preprocessor',
        webpackPreprocessor({
            webpackOptions,
            typescript: require.resolve('typescript'),
        }),
    );

    config.env.processingElements = readProcessingElements();

    on('task', {
        deleteFolder(folderName) {
            console.log('deleting folder %s', folderName);
            return new Promise((resolve, reject) => {
                if (fs.existsSync(folderName)) {
                    rmdir(
                        folderName,
                        { maxRetries: 10, recursive: true },
                        err => {
                            if (err) {
                                console.error(err);
                                return reject(err);
                            }
                            resolve(null);
                        },
                    );
                } else {
                    console.log(
                        'download folder %s does not exist',
                        folderName,
                    );
                    resolve(null);
                }
            });
        },
    });

    return config;
};
