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

import * as angular from 'angular';
import 'angular-material-icons';
import 'angular-material';
import 'angular-loading-bar';

import stateConfig from './state.config'
import iconProviderConfig from './icon-provider.config'
import httpProviderConfig from './http-provider.config'
import nagPrism from './prism/nag-prism.directive'

export default angular.module('sp.core', ['ui.router', 'ui.router.upgrade', 'ngMaterial', 'ngMdIcons', 'angular-loading-bar'])
    .config(stateConfig)
    .config(['cfpLoadingBarProvider', function(cfpLoadingBarProvider) {
        cfpLoadingBarProvider.latencyThreshold = 500;
    }])
    .config(iconProviderConfig)
    .config(httpProviderConfig)
    .directive('nagPrism', nagPrism)
    .name;