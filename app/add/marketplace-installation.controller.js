MarketplaceInstallationController.$inject = ['$scope', 'restApi', '$mdDialog', 'app'];

export default function MarketplaceInstallationController($scope, restApi, $mdDialog, app) {
	$scope.app = app;
	$scope.installing = false;
	$scope.installed = false;
	$scope.installationMessage = {};
	$scope.availableTargetPods;
	
	$scope.hide = function() {
  		$mdDialog.hide();
  	};
  	
  	$scope.cancel = function() {
  	    $mdDialog.cancel();
  	};
  	
  	var loadTargetPods = function () {
        restApi.getTargetPods()
            .success(function (pods) {
            	$scope.availableTargetPods = pods;
            })
            .error(function (error) {
                $scope.status = 'Unable to load pods: ' + error.message;
            });
    }
  	
  	loadTargetPods();
  	
  	$scope.installApp = function(bundle) {
		$scope.installing = true;
		$scope.installLabel = "Installing..."
		restApi.installApp(bundle)
        .success(function (message) {
        	$scope.installing = false;
        	$scope.installed=true;
        	$scope.installLabel = "Installed";
        	$scope.installationMessage = message;
        	console.log(message);
        	$scope.loadAvailableApps();
        })
        .error(function (error) {
            $scope.status = 'Unable to load actions: ' + error.message;
        });
	}
};
