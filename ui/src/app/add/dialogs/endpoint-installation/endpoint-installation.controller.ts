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

export class EndpointInstallationController {

    $mdDialog: any;
    RestApi: any;
    endpointItems : any;
    install: any;
    endpointItemsToInstall: any;
    installationStatus: any;
    installationFinished: any;
    page: any;
    nextButton: any;
    installationRunning: any;
    getEndpointItems: any;

    constructor($mdDialog, RestApi, endpointItems, install, getEndpointItems) {
        this.$mdDialog = $mdDialog;
        this.RestApi = RestApi;
        this.endpointItems = endpointItems;
        this.install = install;
        this.endpointItemsToInstall = endpointItems;
        this.installationStatus = [];
        this.installationFinished = false;
        this.page = "preview";
        this.install = install;
        this.nextButton = "Next";
        this.installationRunning = false;
        this.getEndpointItems = getEndpointItems;
    }

    hide() {
        this.$mdDialog.hide();
    }

    cancel() {
        this.$mdDialog.cancel();
    }

    next() {
        if (this.page === "installation") {
            this.cancel();
        } else {
            this.page = "installation";
            this.initiateInstallation(this.endpointItemsToInstall[0], 0);
        }
    }

    initiateInstallation(endpointUri, index) {
        this.installationRunning = true;
        this.installationStatus.push({"name": endpointUri.name, "id": index, "status": "waiting"});
        if (this.install) {
            this.installElement(endpointUri, index);
        } else {
            this.uninstallElement(endpointUri, index);
        }
    }


    installElement(endpointUri, index) {
        endpointUri = encodeURIComponent(endpointUri.uri);

        this.RestApi.add(endpointUri, true)
            .then(msg => {
                let data = msg.data;
                if (data.success) {
                    this.installationStatus[index].status = "success";
                } else {
                    this.installationStatus[index].status = "error";
                    this.installationStatus[index].details = data.notifications[0].additionalInformation;
                }
            }, data => {
                this.installationStatus[index].status = "error";
            })
            .then(() => {
                if (index < this.endpointItemsToInstall.length - 1) {
                    index++;
                    this.initiateInstallation(this.endpointItemsToInstall[index], index);
                } else {
                    this.getEndpointItems();
                    this.nextButton = "Close";
                    this.installationRunning = false;
                }
            });
    }

    uninstallElement(endpointUri, index) {
        this.RestApi.del(endpointUri.uri)
            .then(msg => {
                let data = msg.data;
                if (data.success) {
                    this.installationStatus[index].status = "success";
                } else {
                    this.installationStatus[index].status = "error";
                }
            }, data => {
                this.installationStatus[index].status = "error";
            })
            .then(() => {
                if (index < this.endpointItemsToInstall.length - 1) {
                    index++;
                    this.initiateInstallation(this.endpointItemsToInstall[index], index);
                } else {
                    this.nextButton = "Close";
                    this.installationRunning = false;
                    this.getEndpointItems();
                }
            });
    }
}

EndpointInstallationController.$inject = ['$mdDialog', 'RestApi', 'endpointItems', 'install', 'getEndpointItems'];