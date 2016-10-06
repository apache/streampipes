import AddEndpointController from './add-endpoint.controller';
import EndpointInstallationController from './endpoint-installation.controller';
import angular from 'npm/angular';

AddCtrl.$inject = ['$scope', 'restApi', '$mdDialog', 'getElementIconText'];

export default function AddCtrl($scope, restApi, $mdDialog, getElementIconText) {

    $scope.elements = "";
    $scope.results = [];
    $scope.loading = false;
    $scope.marketplace = false;
    $scope.endpointItems = [];
    $scope.showInstalled = true;
    $scope.endpointItemsLoadingComplete = false;

    $scope.iconText = function (elementName) {
        return getElementIconText(elementName);
    }

    $scope.selectedTab = "MARKETPLACE";

    $scope.setSelectedTab = function (type) {
        $scope.selectedTab = type;
    }

    $scope.isSelected = function (endpointItem) {
        return endpointItem.selected;
    }

    $scope.getSelectedBackground = function (endpointItem) {
        if (endpointItem.selected) return "#EEEEEE";
        else return "#FFFFFF";
    }

    $scope.toggleSelected = function (endpointItem) {
        if (!endpointItem.selected) endpointItem.selected = true;
        else endpointItem.selected = false;
    }

    $scope.selectAll = function (selected) {
        angular.forEach($scope.endpointItems, function (item) {
            if (item.type == $scope.selectedTab) item.selected = selected;
        })
    }

    $scope.getTitle = function (selectedTab) {
        if (selectedTab == 'source') {
            return "Data Sources";
        } else if (selectedTab == 'sepa') {
            return "Processing Elements";
        } else if (selectedTab == 'action') {
            return "Data Sinks";
        } else {
            return "Marketplace";
        }
    }

    $scope.showManageRdfEndpointsDialog = function () {
        $mdDialog.show({
            controller: AddEndpointController,
            templateUrl: 'app/add/templates/add-endpoint.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            scope: $scope,
            preserveScope: true
        })
    };

    $scope.getEndpointItems = function () {
        $scope.endpointItemsLoadingComplete = false;
        restApi.getRdfEndpointItems()
            .success(function (endpointItems) {
                $scope.endpointItems = endpointItems;
                $scope.endpointItemsLoadingComplete = true;
            })
            .error(function (error) {
                console.log(error);
            });
    }

    $scope.addFromEndpoint = function (endpointUrl) {
        console.log(endpointUrl);
        $scope.loading = true;
        restApi.addBatch(endpointUrl, true)
            .success(function (data) {
                $scope.loading = false;
                angular.forEach(data, function (element, index) {
                    $scope.results[index] = {};
                    $scope.results[index].success = element.success;
                    $scope.results[index].elementName = element.elementName;
                    $scope.results[index].details = [];
                    angular.forEach(element.notifications, function (notification, i) {
                        var detail = {};
                        detail.description = notification.description;
                        detail.title = notification.title;
                        $scope.results[index].details.push(detail);
                    })
                });
            })
    }

    $scope.add = function (descriptionUrls) {
        $scope.loading = true;
        var uris = descriptionUrls.split(" ");
        $scope.addElements(uris, 0);
    }

    $scope.installSingleElement = function (endpointItem) {
        var endpointItems = [];
        endpointItems.push(endpointItem);
        $scope.installElements(endpointItems, true);
    }

    $scope.uninstallSingleElement = function (endpointItem) {
        var endpointItems = [];
        endpointItems.push(endpointItem);
        $scope.installElements(endpointItems, false);
    }

    $scope.installSelected = function() {
        $scope.installElements(getSelectedElements(true), true);
    }

    $scope.uninstallSelected = function() {
        $scope.installElements(getSelectedElements(false), false);
    }

    var getSelectedElements = function(install) {
        var elementsToInstall = [];

        angular.forEach($scope.endpointItems, function (item) {
            if (item.type == $scope.selectedTab) {
                if (item.installed == !install && item.selected) {
                    elementsToInstall.push(item);
                }
            }
        });

        return elementsToInstall;
    }

    $scope.installElements = function (endpointItems, install) {
        $mdDialog.show({
            controller: EndpointInstallationController,
            templateUrl: 'app/add/endpoint-installation.tmpl.html',
            parent: angular.element(document.body),
            clickOutsideToClose: false,
            scope: $scope,
            preserveScope: true,
            locals: {
                endpointItems: endpointItems,
                install: install
            }
        });
    }

    $scope.getEndpointItems();
};
