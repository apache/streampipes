export default function DialogController($scope, $mdDialog, restApi) {
	$scope.namespaces = [];
	$scope.addSelected = false;
	$scope.newNamespace = {};

	$scope.getNamespaces = function() {
		restApi.getOntologyNamespaces()
			.success(function(namespaces){
				$scope.namespaces = namespaces;
			})
			.error(function(msg){
				console.log(msg);
			});
	}

	$scope.addNamespace = function() {
		restApi.addOntologyNamespace($scope.newNamespace)
			.success(function(msg){
				$scope.addSelected = false;
				$scope.newNamespace = {};
				$scope.getNamespaces();
			})
			.error(function(msg){
				$scope.addSelected = false;
				console.log(msg);
			}); 		
	}

	$scope.deleteNamespace = function(prefix) {
		restApi.deleteOntologyNamespace(prefix)
			.success(function(msg){
				$scope.getNamespaces();
			})
			.error(function(msg){
				console.log(msg);
			}); 	
	}

	$scope.showAddInput = function() {
		$scope.addSelected = true;
		$scope.newNamespace.prefix = "";
		$scope.newNamespace.name = "";
	}

	$scope.hide = function() {
		$mdDialog.hide();
	};

	$scope.cancel = function() {
		$mdDialog.cancel();
	};

	$scope.getNamespaces();
}
