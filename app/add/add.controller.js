import {AddEndpointController} from './dialogs/add-endpoint/add-endpoint.controller';
import {EndpointInstallationController} from './dialogs/endpoint-installation/endpoint-installation.controller';
import angular from 'angular';

export class AddCtrl {

    constructor(restApi, $mdDialog, getElementIconText) {
        this.restApi = restApi;
        this.$mdDialog = $mdDialog;
        this.getElementIconText = getElementIconText;
        this.elements = "";
        this.results = [];
        this.loading = false;
        this.endpointItems = [];
        this.endpointItemsLoadingComplete = false;
        this.selectedTab = "MARKETPLACE";
        this.getEndpointItems();
    }

    iconText(elementName) {
        return this.getElementIconText(elementName);
    }

    setSelectedTab(type) {
        this.selectedTab = type;
    }

    isSelected(endpointItem) {
        return endpointItem.selected;
    }

    getSelectedBackground(endpointItem) {
        if (endpointItem.selected) return "#EEEEEE";
        else return "#FFFFFF";
    }

    toggleSelected(endpointItem) {
        endpointItem.selected = !endpointItem.selected;
    }

    selectAll(selected) {
        this.endpointItems.forEach(item => {
            if (item.type === this.selectedTab) item.selected = selected;
        });
    }

    getTitle(selectedTab) {
        if (selectedTab === 'source') {
            return "Data Sources";
        } else if (selectedTab === 'sepa') {
            return "Processing Elements";
        } else if (selectedTab === 'action') {
            return "Data Sinks";
        } else {
            return "Marketplace";
        }
    }

    showManageRdfEndpointsDialog() {
        this.$mdDialog.show({
            controller: AddEndpointController,
            controllerAs: 'ctrl',
            templateUrl: 'app/add/dialogs/add-endpoint/add-endpoint.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            locals: {
                getEndpointItems: this.getEndpointItems
            },
            bindToController: true
        })
    }

    getEndpointItems() {
        this.endpointItemsLoadingComplete = false;
        this.restApi.getRdfEndpointItems()
            .success(endpointItems => {
                this.endpointItems = endpointItems;
                this.endpointItemsLoadingComplete = true;
            })
            .error(error => {
                console.log(error);
            });
    }

    addFromEndpoint(endpointUrl) {
        this.loading = true;
        this.restApi.addBatch(endpointUrl, true)
            .success(data => {
                this.loading = false;
                data.forEach((element, index) => {
                    this.results[index] = {};
                    this.results[index].success = element.success;
                    this.results[index].elementName = element.elementName;
                    this.results[index].details = [];
                    element.notifications.forEach(notification => {
                        let detail = {};
                        detail.description = notification.description;
                        detail.title = notification.title;
                        this.results[index].details.push(detail);
                    })
                });
            })
    }

    installSingleElement(endpointItem) {
        let endpointItems = [];
        endpointItems.push(endpointItem);
        this.installElements(endpointItems, true);
    }

    uninstallSingleElement(endpointItem) {
        let endpointItems = [];
        endpointItems.push(endpointItem);
        this.installElements(endpointItems, false);
    }

    installSelected() {
        this.installElements(this.getSelectedElements(true), true);
    }

    uninstallSelected() {
        this.installElements(this.getSelectedElements(false), false);
    }

    getSelectedElements(install) {
        let elementsToInstall = [];

        this.endpointItems.forEach(item => {
            if (item.type === this.selectedTab) {
                if (item.installed === !install && item.selected) {
                    elementsToInstall.push(item);
                }
            }
        });

        return elementsToInstall;
    }

    installElements(endpointItems, install) {
        this.$mdDialog.show({
            controller: EndpointInstallationController,
            controllerAs: 'ctrl',
            templateUrl: 'app/add/dialogs/endpoint-installation/endpoint-installation.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: false,
            locals: {
                endpointItems: endpointItems,
                install: install,
                getEndpointItems: this.getEndpointItems
            },
            bindToController: true
        });
    }

}

AddCtrl.$inject = ['restApi', '$mdDialog', 'getElementIconText'];