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

export class DeploymentController {

    DeploymentService: any;
    deployment: any;
    resultReturned: any;
    loading: any;
    jsonld: any;
    zipFile: any;
    deploymentSettings: any;
    element: any;
    java: any;

    constructor(DeploymentService) {
        this.DeploymentService = DeploymentService;
        this.deployment = {};

        this.resultReturned = false;
        this.loading = false;
        this.jsonld = "";
        this.zipFile = "";
    }

    $onInit() {
        this.deployment.elementType = this.deploymentSettings.elementType;
    }

    generateImplementation() {
        this.resultReturned = false;
        this.loading = true;
        this.DeploymentService.generateImplementation(this.deployment, this.element)
            .success((data, status, headers, config) => {
                //$scope.openSaveAsDialog($scope.deployment.artifactId +".zip", data, "application/zip");
                this.resultReturned = true;
                this.loading = false;
                this.zipFile = data;
            }).error((data, status, headers, config) => {
            this.loading = false;
        });
    };

    generateDescription() {
        this.loading = true;
        this.DeploymentService.generateDescriptionJava(this.deployment, this.element)
            .success((data, status, headers, config) => {
                // $scope.openSaveAsDialog($scope.element.name +".jsonld", data, "application/json");
                this.loading = false;
                this.resultReturned = true;
                this.java = data;
            }).error((data, status, headers, config) => {
            this.loading = false;
        });
        this.DeploymentService.generateDescriptionJsonld(this.deployment, this.element)
            .success((data, status, headers, config) => {
                // $scope.openSaveAsDialog($scope.element.name +".jsonld", data, "application/json");
                this.loading = false;
                this.resultReturned = true;
                this.jsonld = JSON.stringify(data, null, 2);
            }).error((data, status, headers, config) => {
            this.loading = false;
        });
    }


    openSaveAsDialog(filename, content, mediaType) {
        var blob = new Blob([content], {type: mediaType});
        // TODO: saveAs not implemented
        //this.saveAs(blob, filename);
    }
}

DeploymentController.$inject = ['DeploymentService'];