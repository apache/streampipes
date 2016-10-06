AddEndpointController.$inject = ['$scope', '$mdDialog', 'restApi'];

export default function AddEndpointController($scope, $mdDialog, restApi) {

    $scope.rdfEndpoints = [];
    $scope.addSelected = false;

    $scope.newEndpoint = {};

    $scope.showAddInput = function() {
        $scope.addSelected = true;
    }

    var loadRdfEndpoints = function() {
        restApi.getRdfEndpoints()
            .success(function (rdfEndpoints) {
                $scope.rdfEndpoints = rdfEndpoints;
            })
            .error(function (error) {
               console.log(error);
            });
    }

    $scope.addRdfEndpoint = function(rdfEndpoint) {
        restApi.addRdfEndpoint(rdfEndpoint)
            .success(function (message) {
                loadRdfEndpoints();
                $scope.getEndpointItems();
            })
            .error(function (error) {
                console.log(error);
            });
    }

    $scope.removeRdfEndpoint = function(rdfEndpointId) {
        restApi.removeRdfEndpoint(rdfEndpointId)
            .success(function (message) {
                loadRdfEndpoints();
            })
            .error(function (error) {
                console.log(error);
            });
    }

    loadRdfEndpoints();

    $scope.hide = function () {
        $mdDialog.hide();
    };

    $scope.cancel = function () {
        $mdDialog.cancel();
    };
}