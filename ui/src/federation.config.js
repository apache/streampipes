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
    shareAll,
} = require('@angular-architects/native-federation/config');

module.exports = withNativeFederation({
    shared: {
        // '@angular/core': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/core/primitives/signals': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/common': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/common/http': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/forms': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/router': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@ngbracket/ngx-layout': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   includeSecondaries: true
        // },
        // '@ngbracket/ngx-layout/core': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   includeSecondaries: true
        // },
        // '@ngbracket/ngx-layout/extended': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   includeSecondaries: true
        // },
        // '@ngbracket/ngx-layout/_private-utils': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   includeSecondaries: true
        // },
        // '@ngbracket/ngx-layout/flex': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   includeSecondaries: true
        // },
        // '@ngbracket/ngx-layout/grid': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   includeSecondaries: true
        // },
        // '@angular/material': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   includeSecondaries: true
        // },
        // '@angular/material/core': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   includeSecondaries: true
        // },
        // '@angular/material/button': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material/progress-bar': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/cdk': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        ...shareAll({
            singleton: true,
            strictVersion: true,
            requiredVersion: 'auto',
        }),
    },

    skip: [
        'material-icons',
        'rxjs/ajax',
        'rxjs/fetch',
        'rxjs/testing',
        'rxjs/webSocket',
        'jquery',
        'jquery-ui-dist',
        'd3-array/umd',
        'd3-array/default',
        'd3-contour/umd',
        'd3-contour/default',
        'd3-contour',
        'konva',
        pkg => pkg.startsWith('marked'),
        pkg => pkg.startsWith('swagger-ui'),
        pkg => pkg.startsWith('date-fns'),
        'roboto-fontface',
        // '@angular/platform-server',
        // 'ngx-markdown',
        // 'date-fns',
        // '@ngbracket/ngx-layout/server',
        // (pkg) => pkg.startsWith('echarts'),
        // 'echarts-gl',
        // 'echarts-wordcloud',
        // 'file-saver',
        // 'ngx-quill',
        // 'ngx-markdown',
        // 'quill',
        // 'shepherd.js',
        // 'stream-browserify',
        // 'tslib',
        pkg => {
            return (
                (!pkg.startsWith('@angular') &&
                    !pkg.startsWith('@ngbracket')) ||
                pkg.startsWith('@angular/platform-server') ||
                pkg.startsWith('@ngbracket/ngx-layout/server')
            );
        },
        // Add further packages you don't need at runtime
    ],
});
