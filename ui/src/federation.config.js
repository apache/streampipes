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

const {
    withNativeFederation,
    share,
} = require('@angular-architects/native-federation/config');

module.exports = withNativeFederation({
    shared: {
        ...share(
            [
                '@angular/animations',
                '@angular/core',
                '@angular/common',
                '@angular/compiler',
                '@angular/forms',
                '@angular/platform-browser',
                '@angular/platform-browser-dynamic',
                '@angular/router',
                '@angular/cdk',
                '@angular/material',
                'echarts',
                'ngx-echarts',
                '@ngx-translate/core',
                '@ngx-translate/http-loader',
                'rxjs',
            ].reduce(
                (acc, name) => ({
                    ...acc,
                    [name]: {
                        singleton: true,
                        strictVersion: true,
                        requiredVersion: 'auto',
                        includeSecondaries: true,
                    },
                }),
                {},
            ),
        ),
    },

    skip: [],
});
