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

import {RestApi} from "../../../services/rest-api.service";

export class FileUploadController {

    file: any;
    restApi: RestApi;
    selectedElement: any;
    staticProperty: any;

    fileMetadataDescriptions: any = [];
    showFiles: boolean;

    $scope: any;
    $rootScope: any;

    constructor(restApi: RestApi, $scope, $rootScope) {
        this.restApi = restApi;
        this.$scope = $scope;
        this.$rootScope = $rootScope;
    }

    $onInit() {
        this.showFiles = false;
        this.getFileMetadata();


        this.$scope.$watch(() => this.staticProperty.properties.locationPath, (newValue, oldValue) => {
            if (this.staticProperty.properties.locationPath !== "" && this.staticProperty.properties.locationPath != undefined) {
                if (newValue !== oldValue) {
                    this.$rootScope.$emit(this.staticProperty.properties.internalName);
                }
            }
        });
    }

    uploadFile() {
        let fileInput: any = document.getElementById('file');
        let file = fileInput.files[0];
        let filename = fileInput.files[0].name;

        const data: FormData = new FormData();
        data.append('file_upload', file, filename);

        this.restApi.uploadFile(data).then(result => {
            this.getFileMetadata();
        });
    }

    getFileMetadata() {
        this.restApi.getFileMetadata().then(fm => {
            this.fileMetadataDescriptions = fm.data;
            this.showFiles = true;
        })
    }
}

FileUploadController.$inject = ['RestApi', '$scope', '$rootScope'];