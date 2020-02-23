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

import {ShepherdService} from "../services/tour/shepherd.service";

export class LoginCtrl {

    $timeout: any;
    $log: any;
    $location: any;
    $state: any;
    $stateParams: any;
    $window: any;
    RestApi: any;
    AuthStatusService: any;
    loading: any;
    authenticationFailed: any;
    credentials: any;
    ShepherdService: ShepherdService;

    constructor($timeout, $log, $location, $state, $stateParams, RestApi, $window, AuthStatusService, ShepherdService) {
        this.$timeout = $timeout;
        this.$log = $log;
        this.$location = $location;
        this.$state = $state;
        this.$stateParams = $stateParams;
        this.$window = $window;
        this.RestApi = RestApi;
        this.AuthStatusService = AuthStatusService;

        this.ShepherdService = ShepherdService;

        this.loading = false;
        this.authenticationFailed = false;
    }


    openDocumentation(){
        this.$window.open('https://docs.streampipes.org', '_blank');
    };

    logIn() {
        this.authenticationFailed = false;
        this.loading = true;
        this.RestApi.login(this.credentials)
            .then(response => { // success
                    this.loading = false;
                    if (response.data.success) {
                        this.AuthStatusService.username = response.data.info.authc.principal.username;
                        this.AuthStatusService.email = response.data.info.authc.principal.email;
                        this.AuthStatusService.token = response.data.token;
                        this.AuthStatusService.authenticated = true;
                        this.$state.go("streampipes");
                    }
                    else {
                        this.AuthStatusService.authenticated = false;
                        this.authenticationFailed = true;
                    }

                }, response => { // error
                    this.loading = false;
                    this.AuthStatusService.authenticated = false;
                    this.authenticationFailed = true;
                }
            )
    };

    setSheperdServiceDelay() {
        this.ShepherdService.setTimeWaitMillies(100);
    }
};

LoginCtrl.$inject = ['$timeout', '$log', '$location', '$state', '$stateParams', 'RestApi', '$window', 'AuthStatusService', 'ShepherdService'];
