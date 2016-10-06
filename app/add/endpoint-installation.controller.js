EndpointInstallationController.$inject = ['$scope', '$mdDialog', 'restApi', 'endpointItems', 'install'];

export default function EndpointInstallationController($scope, $mdDialog, restApi, endpointItems, install) {

    $scope.endpointItemsToInstall = endpointItems;
    $scope.installationStatus = [];
    $scope.installationFinished = false;
    $scope.page = "preview";
    $scope.install = install;

    $scope.hide = function () {
        $mdDialog.hide();
    };

    $scope.cancel = function () {
        $mdDialog.cancel();
    };

    $scope.next = function () {
        $scope.page = "installation";
        initiateInstallation($scope.endpointItemsToInstall[0], 0);
    }

    var initiateInstallation = function (endpointUri, index) {
        console.log(endpointUri);
        $scope.installationStatus.push({"name": endpointUri.name, "id": index, "status": "waiting"});
        if (install) {
            installElement(endpointUri, index);
        } else {
            uninstallElement(endpointUri, index);
        }
    }

    var installElement = function (endpointUri, index) {
        endpointUri = encodeURIComponent(endpointUri.uri);

        restApi.add(endpointUri, true)
            .success(function (data) {
                if (data.success) {
                    $scope.installationStatus[index].status = "success";
                } else {
                    $scope.installationStatus[index].status = "error";
                }
            })
            .error(function (data) {
                $scope.installationStatus[index].status = "error";
            })
            .then(function () {
                if (index < $scope.endpointItemsToInstall.length - 1) {
                    index++;
                    initiateInstallation($scope.endpointItemsToInstall[index], index);
                } else {
                    $scope.getEndpointItems();
                }
            });

    }

    var uninstallElement = function (endpointUri, index) {
        //endpointUri = encodeURIComponent(endpointUri.uri);
        restApi.del(endpointUri.uri).success(function (data) {
            if (data.success) {
                $scope.installationStatus[index].status = "success";
            } else {
                $scope.installationStatus[index].status = "error";
            }
        })
            .error(function (data) {
                $scope.installationStatus[index].status = "error";
            })
            .then(function () {
                if (index < $scope.endpointItemsToInstall.length - 1) {
                    index++;
                    initiateInstallation($scope.endpointItemsToInstall[index], index);
                } else {
                    $scope.getEndpointItems();
                }
            });
    }
}