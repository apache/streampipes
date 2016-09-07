export default function AddDialogController($scope, $mdDialog, restApi, elementType, conceptId) {

	$scope.elementData = {};
	$scope.elementData.namespace = "";
	$scope.elementData.id = "";
	$scope.elementData.elementName = "";
	//$scope.elementData.instanceOf = conceptId;
	$scope.elementType = elementType;
	$scope.conceptId = conceptId;
	$scope.namespaces = [];

	$scope.getNamespaces = function() {
		restApi.getOntologyNamespaces()
			.success(function(namespaces){
				$scope.namespaces = namespaces;
			})
			.error(function(msg){
				console.log(msg);
			});
	}

	$scope.add = function() {
		var promise;
		if (elementType === 'Property') 
		{
			restApi.addOntologyProperty($scope.elementData)
				.success(function(msg){
					$scope.loadProperties();
					$scope.loadPropertyDetails($scope.elementData.namespace +$scope.elementData.elementName);
				});
		}
		else if (elementType === 'Concept') 
		{
			restApi.addOntologyConcept($scope.elementData)
				.success(function(msg){
					$scope.loadConcepts();
					$scope.loadConceptDetails($scope.elementData.namespace +$scope.elementData.elementName);
				});
		}
		else 
		{
			if ($scope.conceptId != undefined) $scope.elementData.instanceOf = conceptId;
			$scope.elementData.id=$scope.elementData.namespace +$scope.elementData.elementName
			restApi.addOntologyInstance($scope.elementData).success(function(msg){

				$scope.loadConcepts();
				if ($scope.conceptId != undefined) $scope.loadConceptDetails(conceptId);
				$scope.loadInstanceDetails($scope.elementData.namespace +$scope.elementData.elementName);
			});
		}

		$scope.hide();
	};

	$scope.hide = function() {
		$mdDialog.hide();
	};

	$scope.cancel = function() {
		$mdDialog.cancel();
	};

	$scope.getNamespaces();
}
