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

const webpack = require('webpack')
const path = require('path');

const ModuleFederationPlugin = require("webpack/lib/container/ModuleFederationPlugin");
const mf = require("@angular-architects/module-federation/webpack");
const share = mf.share;

const sharedMappings = new mf.SharedMappings();
sharedMappings.register(
    path.join(__dirname, 'tsconfig.json'),
    [/* mapped paths to share */]);


module.exports = {
    module: {
        rules: [
            { test: /\.html$/, loader: 'raw-loader' }
        ],
    },
    experiments: {
        outputModule: true
    },
    resolve: {
        alias: {
            npm: path.join(__dirname, 'node_modules'),
            legacy: path.join(__dirname, 'src', 'assets', 'lib'),
        },
        fallback: {
					"assert": false,
					"stream": require.resolve("stream-browserify")
        }
    },
    plugins: [
        new webpack.ProvidePlugin({
            process: 'process/browser',
        }),
        new ModuleFederationPlugin({

            library: { type: "module" },

            name: "sp",
            filename: "remoteEntry.js",
            exposes: { },

            shared: share({
                "@angular/core": { singleton: true, strictVersion: true, requiredVersion: 'auto', eager: true },
                "@angular/common": { singleton: true, strictVersion: true, requiredVersion: 'auto' , eager: true},
                "@angular/common/http": { singleton: true, strictVersion: true, requiredVersion: 'auto' , eager: true},
                "@angular/router": { singleton: true, strictVersion: true, requiredVersion: 'auto' , eager: true},
                //"@streampipes/platform-services": {singleton: true, strictVersion: true, requiredVersion: 'auto', eager: true},

                ...sharedMappings.getDescriptors()
            })

        }),
        sharedMappings.getPlugin()
    ]
};
