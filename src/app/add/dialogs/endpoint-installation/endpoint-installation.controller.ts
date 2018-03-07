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

    constructor($mdDialog, RestApi, endpointItems, install) {
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


    // TODO: getEndpointItems not implemented
    installElement(endpointUri, index) {
        endpointUri = encodeURIComponent(endpointUri.uri);

        this.RestApi.add(endpointUri, true)
            .success(data => {
                if (data.success) {
                    this.installationStatus[index].status = "success";
                } else {
                    this.installationStatus[index].status = "error";
                    this.installationStatus[index].details = data.notifications[0].additionalInformation;
                }
            })
            .error(data => {
                this.installationStatus[index].status = "error";
            })
            .then(() => {
                if (index < this.endpointItemsToInstall.length - 1) {
                    index++;
                    this.initiateInstallation(this.endpointItemsToInstall[index], index);
                } else {
                    //this.getEndpointItems();
                    this.nextButton = "Close";
                    this.installationRunning = false;
                }
            });
    }

    // TODO: getEndpointItems not implemented
    uninstallElement(endpointUri, index) {
        this.RestApi.del(endpointUri.uri)
            .success(data => {
                if (data.success) {
                    this.installationStatus[index].status = "success";
                } else {
                    this.installationStatus[index].status = "error";
                }
            })
            .error(data => {
                this.installationStatus[index].status = "error";
            })
            .then(() => {
                if (index < this.endpointItemsToInstall.length - 1) {
                    index++;
                    this.initiateInstallation(this.endpointItemsToInstall[index], index);
                } else {
                    this.nextButton = "Close";
                    this.installationRunning = false;
                    //this.getEndpointItems();
                }
            });
    }
}

EndpointInstallationController.$inject = ['$mdDialog', 'RestApi', 'endpointItems', 'install'];