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

export class DeploymentService {

    $http: any;
    $rootScope: any;
    RestApi: any;
    AuthStatusService: any;

    constructor($http, AuthStatusService, RestApi) {
        this.$http = $http;
        this.AuthStatusService = AuthStatusService;
        this.RestApi = RestApi;
    }

    updateElement(deploymentConfig, model) {
        return this.$http({
            method: 'POST',
            headers: {'Accept': 'application/json', 'Content-Type': undefined},
            url: '/streampipes-backend/api/v2/users/' + this.AuthStatusService.email + '/deploy/update',
            data: this.getFormData(deploymentConfig, model)
        });
    }

    generateImplementation(deploymentConfig, model) {
        return this.$http({
            method: 'POST',
            responseType: 'arraybuffer',
            headers: {'Accept': 'application/zip', 'Content-Type': undefined},
            url: '/streampipes-backend/api/v2/users/' + this.AuthStatusService.email + '/deploy/implementation',
            data: this.getFormData(deploymentConfig, model)
        });
    }

    generateDescriptionJava(deploymentConfig, model) {
        return this.$http({
            method: 'POST',
            headers: {'Accept': 'text/plain', 'Content-Type': undefined},
            url: '/streampipes-backend/api/v2/users/' + this.$rootScope.email + '/deploy/description/java',
            data: this.getFormData(deploymentConfig, model)
        });
    }

    generateDescriptionJsonld(deploymentConfig, model) {
        return this.$http({
            method: 'POST',
            headers: {'Accept': 'application/json', 'Content-Type': undefined},
            url: '/streampipes-backend/api/v2/users/' + this.AuthStatusService.email + '/deploy/description/jsonld',
            data: this.getFormData(deploymentConfig, model)
        });
    }

    getFormData(deploymentConfig, model) {
        var formData = new FormData();
        formData.append("config", angular.toJson(deploymentConfig));
        formData.append("model", angular.toJson(model));
        return formData;
    }

}

//DeploymentService.$inject = ['$http', '$rootScope', 'RestApi'];