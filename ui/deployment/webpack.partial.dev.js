/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

/**
 *
 * Note: to change this file, change webpack.partial.dev.js in deployment folder
 * changes in the root folder will be overridden by prebuild script
 *
 */

const { merge } = require('webpack-merge');
const baseConfig = require('./webpack.partial.base.js');

module.exports = merge(baseConfig, {
    devServer: {
        compress: true,
        port: 8082,
        proxy: {
            '/streampipes-connect': {
                target: 'http://localhost:8030',
                // pathRewrite: { '^/streampipes-connect': '' },
                secure: false,
            },
            '/streampipes-backend': {
                target: 'http://localhost:8030',
                secure: false,
            },
            '/visualizablepipeline': {
                target: 'http://localhost:5984',
                secure: false,
            },
            '/dashboard': {
                target: 'http://localhost:5984',
                secure: false,
            },
            '/widget': {
                target: 'http://localhost:5984',
                secure: false,
            },
            '/pipeline': {
                target: 'http://localhost:5984',
                secure: false,
            },
        },
    },
});
