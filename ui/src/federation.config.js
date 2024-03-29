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
        // ...shareAll({
        //     singleton: true,
        //     strictVersion: true,
        //     requiredVersion: 'auto',
        // }),
        '@angular/animations': {
            singleton: true,
            strictVersion: true,
            requiredVersion: 'auto',
            eager: true,
        },
        '@angular/animations/browser': {
            singleton: true,
            strictVersion: true,
            requiredVersion: 'auto',
            eager: true,
        },
        '@angular/core': {
            singleton: true,
            strictVersion: true,
            requiredVersion: 'auto',
            eager: true,
        },
        '@angular/common': {
            singleton: true,
            strictVersion: true,
            requiredVersion: 'auto',
            eager: true,
        },
        '@angular/common/http': {
            singleton: true,
            strictVersion: true,
            requiredVersion: 'auto',
            eager: true,
        },
        '@angular/compiler': {
            singleton: true,
            strictVersion: true,
            requiredVersion: 'auto',
            eager: true,
        },
        '@angular/forms': {
            singleton: true,
            strictVersion: true,
            requiredVersion: 'auto',
            eager: true,
        },
        '@angular/platform-browser': {
            singleton: true,
            strictVersion: true,
            requiredVersion: 'auto',
            eager: true,
        },
        '@angular/platform-browser/animations': {
            singleton: true,
            strictVersion: true,
            requiredVersion: 'auto',
            eager: true,
        },
        '@angular/platform-browser-dynamic': {
            singleton: true,
            strictVersion: true,
            requiredVersion: 'auto',
            eager: true,
        },
        '@angular/router': {
            singleton: true,
            strictVersion: true,
            requiredVersion: 'auto',
            eager: true,
        },
        // '@angular/cdk': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/cdk/a11y': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/cdk/accordion': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/cdk/bidi': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/cdk/coercion': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/cdk/collections': {
        //   singleton: true,
        //   strictVersion: false,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/cdk/keycodes': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/cdk/layout': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/cdk/observers': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/cdk/platform': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/cdk/scrolling': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/cdk/stepper': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/cdk/table': {
        //   singleton: true,
        //   strictVersion: false,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/cdk/overlay': {
        //   singleton: true,
        //   strictVersion: false,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/cdk/portal': {
        //   singleton: true,
        //   strictVersion: false,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material/autocomplete': {
        //   singleton: true,
        //   strictVersion: false,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material/core': {
        //   singleton: true,
        //   strictVersion: false,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material/button': {
        //   singleton: true,
        //   strictVersion: false,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material/checkbox': {
        //   singleton: true,
        //   strictVersion: false,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material/grid-list': {
        //   singleton: true,
        //   strictVersion: false,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material/icon': {
        //   singleton: true,
        //   strictVersion: false,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material/input': {
        //   singleton: true,
        //   strictVersion: false,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material/list': {
        //   singleton: true,
        //   strictVersion: false,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material/menu': {
        //   singleton: true,
        //   strictVersion: false,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material/progress-spinner': {
        //   singleton: true,
        //   strictVersion: false,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material/tabs': {
        //   singleton: true,
        //   strictVersion: false,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material/tooltip': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material/dialog': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material/divider': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material/select': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        // '@angular/material/form-field': {
        //   singleton: true,
        //   strictVersion: true,
        //   requiredVersion: 'auto',
        //   eager: true,
        // },
        '@angular/core/primitives/signals': {
            singleton: true,
            strictVersion: true,
            requiredVersion: 'auto',
            eager: true,
        },
    },

    skip: [
        // pkg => {
        //     return (
        //         (!pkg.startsWith('@angular') &&
        //             !pkg.startsWith('@ngbracket') &&
        //             !pkg.startsWith('@streampipes')) ||
        //         pkg.startsWith('@angular/platform-server') ||
        //         pkg.startsWith('@ngbracket/ngx-layout/server')
        //     );
        // },
        // Add further packages you don't need at runtime
    ],
});
