export default function ContextController($scope, $mdDialog, restApi, Upload) {

	$scope.contexts = [];
	$scope.addSelected = false;
	$scope.newContext = {};
	$scope.file = {};

	$scope.availableFormats = ["RDFXML", "JSONLD", "TURTLE", "RDFA"];

	$scope.getContexts = function() {
		restApi.getAvailableContexts()
			.success(function(contexts){
				$scope.contexts = contexts;
			})
			.error(function(msg){
				console.log(msg);
			});
	}

	$scope.deleteContext = function(contextId) {
		restApi.deleteContext(contextId)
			.success(function(msg){
				$scope.getContexts();
			})
			.error(function(msg){
				console.log(msg);
			}); 	
	}

	$scope.showAddInput = function() {
		$scope.addSelected = true;
	}

	$scope.submit = function(file) {
		$scope.f = file;
		if (file) {
			file.upload = Upload.upload({
				url: '/semantic-epa-backend/api/v2/contexts',
				data: {file: file, 'context' : angular.toJson($scope.newContext)}
			});

			file.upload.then(function (response) {
			}, function (response) {
				if (response.status > 0)
					$scope.errorMsg = response.status + ': ' + response.data;
			}, function (evt) {
				file.progress = Math.min(100, parseInt(100.0 * 
					evt.loaded / evt.total));
			});
		}   
	}

	$scope.hide = function() {
		$mdDialog.hide();
	};

	$scope.cancel = function() {
		$mdDialog.cancel();
	};

	$scope.getContexts();
}
