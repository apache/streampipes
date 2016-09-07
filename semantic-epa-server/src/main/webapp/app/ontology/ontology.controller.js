OntologyCtrl.$inject = ['$scope', 'restApi', '$mdToast', '$mdDialog'];
import AddDialogController from './add-dialog.controller';
import DialogController from './dialog.controller';
import ContextController from './context.controller';


export default function OntologyCtrl($scope, restApi, $mdToast, $mdDialog) {

	$scope.primitiveClasses = [{"title" : "String", "description" : "A textual datatype, e.g., 'machine1'", "id" : "http://www.w3.org/2001/XMLSchema#string"},
		{"title" : "Boolean", "description" : "A true/false value", "id" : "http://www.w3.org/2001/XMLSchema#boolean"},
		{"title" : "Integer", "description" : "A whole-numerical datatype, e.g., '1'", "id" : "http://www.w3.org/2001/XMLSchema#integer"},
		{"title" : "Double", "description" : "A floating-point number, e.g., '1.25'", "id" : "http://www.w3.org/2001/XMLSchema#double"}];

$scope.rangeTypes = [{"title" : "Primitive Type", "description" : "A primitive type, e.g., a number or a textual value", "rangeType" : "PRIMITIVE"},
	{"title" : "Enumeration", "description" : "A textual value with a specified value set", "rangeType" : "ENUMERATION"},
	{"title" : "Quantitative Value", "description" : "A numerical value within a specified range", "rangeType" : "QUANTITATIVE_VALUE"}];

$scope.properties = [];
$scope.propertySelected = false;
$scope.propertyDetail = {};
$scope.selectedPrimitive = $scope.primitiveClasses[0];
$scope.selectedRangeType = $scope.rangeTypes[0].rangeType;

$scope.concepts = [];
$scope.conceptSelected = false;
$scope.conceptDetail = {};
$scope.currentlySelectedClassProperty;

$scope.instanceSelected = false;
$scope.instanceDetail = {};
$scope.selectedInstanceProperty = "";

$scope.selectedTab = "CONCEPTS";

$scope.setSelectedTab = function(type) {
	$scope.selectedTab = type;
}

$scope.loadProperties = function(){
	restApi.getOntologyProperties()
		.success(function(propertiesData){
			$scope.properties = propertiesData;
		})
		.error(function(msg){
			console.log(msg);
		});
};

$scope.loadConcepts = function(){
	restApi.getOntologyConcepts()
		.success(function(conceptsData){
			$scope.concepts = conceptsData;
		})
		.error(function(msg){
			console.log(msg);
		});
};

$scope.loadPropertyDetails = function(propertyId){
	restApi.getOntologyPropertyDetails(propertyId)
		.success(function(propertiesData){
			$scope.propertyDetail = propertiesData;
			$scope.propertySelected = true;
		})
		.error(function(msg){
			$scope.propertySelected = false;
			console.log(msg);
		});
};

$scope.loadConceptDetails = function(conceptId){
	$scope.instanceSelected = false;
	restApi.getOntologyConceptDetails(conceptId)
		.success(function(conceptData){
			$scope.conceptDetail = conceptData;
			$scope.conceptSelected = true;
		})
		.error(function(msg){
			$scope.conceptSelected = false;
			console.log(msg);
		});
};

$scope.loadInstanceDetails = function(instanceId){
	restApi.getOntologyInstanceDetails(instanceId)
		.success(function(instanceData){
			$scope.instanceDetail = instanceData;
			$scope.instanceSelected = true;
		})
		.error(function(msg){
			$scope.instanceSelected = false;
			console.log(msg);
		});
};

$scope.addPropertyToClass = function(p) {
	if (!$scope.conceptDetail.domainProperties) $scope.conceptDetail.domainProperties = [];
	restApi.getOntologyPropertyDetails(p)
		.success(function(propertiesData){
			$scope.conceptDetail.domainProperties.push(propertiesData);
		})
		.error(function(msg){
			console.log(msg);
		});
}

$scope.addPropertyToInstance = function() {
	if (!$scope.instanceDetail.domainProperties) $scope.instanceDetail.domainProperties = [];
	restApi.getOntologyPropertyDetails($scope.selectedInstanceProperty)
		.success(function(propertiesData){
			$scope.instanceDetail.domainProperties.push(propertiesData);
		})
		.error(function(msg){
			console.log(msg);
		});

}

$scope.removePropertyFromInstance = function(property) {
	$scope.instanceDetail.domainProperties.splice($scope.instanceDetail.domainProperties.indexOf(property), 1);
}

$scope.removePropertyFromClass = function(property) {
	$scope.conceptDetail.domainProperties.splice($scope.conceptDetail.domainProperties.indexOf(property), 1);
}

$scope.storeClass = function() {
	$scope.loading = true;
	restApi.updateOntologyConcept($scope.conceptDetail.elementHeader.id, $scope.conceptDetail)
		.success(function(msg){
			$scope.loading = false;
			$scope.showToast("Concept updated.");
		})
		.error(function(msg){
			$scope.loading = false;

		});
}

$scope.storeInstance = function() {
	$scope.loading = true;
	restApi.updateOntologyInstance($scope.instanceDetail.elementHeader.id, $scope.instanceDetail)
		.success(function(msg){
			$scope.loading = false;
			$scope.showToast("Instance updated.");
			$scope.loadConcepts();
		})
		.error(function(msg){
			$scope.loading = false;

		});
}

$scope.addTypeDefinition = function() {
	$scope.propertyDetail.range = {};
	$scope.propertyDetail.range.rangeType = $scope.selectedRangeType;
	$scope.propertyDetail.rangeDefined = true;
	if ($scope.selectedRangeType === 'PRIMITIVE')
	{
		$scope.propertyDetail.rdfsDatatype = "";
	} else if ($scope.selectedRangeType === 'QUANTITATIVE_VALUE')
	{
		$scope.propertyDetail.range.minValue = -1;
		$scope.propertyDetail.range.maxValue = -1;
		$scope.propertyDetail.range.unitCode = "";
	} else if ($scope.selectedRangeType === 'ENUMERATION')
	{
		$scope.propertyDetail.range.minValue = -1;
		$scope.propertyDetail.range.maxValue = -1;
		$scope.propertyDetail.range.unitCode = "";
	}
}

$scope.reset = function() {
	$scope.propertyDetail.rangeDefined = false;
}

$scope.updateProperty = function() {
	$scope.loading = true;
	$scope.propertyDetail.labelDefined = true;
	restApi.updateOntologyProperty($scope.propertyDetail.elementHeader.id, $scope.propertyDetail)
		.success(function(msg){
			$scope.loading = false;
			$scope.showToast("Property updated.");
		})
		.error(function(msg){
			$scope.loading = false;

		});
}

$scope.openNamespaceDialog = function(){
	$mdDialog.show({
		controller: DialogController,
		templateUrl: 'app/ontology/templates/manageNamespacesDialog.tmpl.html',
		parent: angular.element(document.body),
		clickOutsideToClose:true,
	})
};

$scope.openAddElementDialog = function(elementType){
	$scope.openAddElementDialog(elementType, undefined);
};

$scope.openAddElementDialog = function(elementType, conceptId){
	$mdDialog.show({
		controller: AddDialogController,
		templateUrl: 'app/ontology/templates/createElementDialog.tmpl.html',
		parent: angular.element(document.body),
		clickOutsideToClose:true,
		scope:$scope,
		preserveScope:true,
		locals : {
			elementType : elementType,
			conceptId : conceptId
		}
	})
}

$scope.deleteConcept = function(conceptId) {
	restApi.deleteOntologyConcept(conceptId)
		.success(function(msg){
			$scope.loadConcepts();
			$scope.conceptSelected = false;
		})
		.error(function(msg){
			console.log(msg);
		}); 
};

$scope.deleteProperty = function(propertyId) {
	restApi.deleteOntologyProperty(propertyId)
		.success(function(msg){
			$scope.loadProperties();
			$scope.propertySelected = false;
		})
		.error(function(msg){
			console.log(msg);
		}); 
};

$scope.deleteInstance = function(instanceId) {
	restApi.deleteOntologyInstance(instanceId)
		.success(function(msg){
			$scope.loadConcepts();
			$scope.instanceSelected = false;
		})
		.error(function(msg){
			console.log(msg);
		}); 
};

$scope.$on('loadProperty', function(event, propertyId) {
	$scope.loadPropertyDetails(propertyId);
});

$scope.loadProperties();
$scope.loadConcepts();


$scope.showToast = function(text) {
	$mdToast.show(
		$mdToast.simple()
		.content(text)
		.position("top right")
		.hideDelay(3000)
	);
}

$scope.openImportDialog = function() {
	$mdDialog.show({
		controller: ContextController,
		templateUrl: 'app/ontology/templates/manageVocabulariesDialog.tmpl.html',
		parent: angular.element(document.body),
		clickOutsideToClose:true,
	})
}

};
