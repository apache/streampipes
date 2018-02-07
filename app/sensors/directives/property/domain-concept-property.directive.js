domainConceptProperty.$inject = ['RestApi'];

export default function domainConceptProperty(RestApi) {
	return {
		restrict : 'AE',
		templateUrl : 'app/sensors/directives/property/domain-concept-property.tmpl.html',
		scope : {
			domainProperty : "=domainProperty",
			disabled : "=disabled"
		},
		link: function(scope, element, attrs) {

			scope.concepts = [];
			scope.properties = [];

			scope.loadProperties = function(){
				RestApi.getOntologyProperties()
					.success(function(propertiesData){
						scope.properties = propertiesData;
					})
					.error(function(msg){
						console.log(msg);
					});
			};

			scope.loadConcepts = function(){
				RestApi.getOntologyConcepts()
					.success(function(conceptsData){
						scope.concepts = conceptsData;
					})
					.error(function(msg){
						console.log(msg);
					});
			};

			scope.loadProperties();
			scope.loadConcepts();


		},
		controller: function($scope, $element) {

			$scope.addSupportedProperty = function(supportedProperties) {   
				if (supportedProperties == undefined) supportedProperties = [];
				supportedProperties.push({"propertyId" : ""});
			}

			$scope.removeSupportedProperty = function(supportedProperties, index) {   	
				supportedProperties.splice(index, 1);
			}		

			$scope.conceptRestricted = function(domainProperty) {
				if (domainProperty.requiredClass == undefined) return false;
				return true;
			};

			$scope.toggleConceptRestriction = function(domainProperty) {
				if ($scope.conceptRestricted(domainProperty)) domainProperty.requiredClass = undefined;
				else domainProperty.requiredClass = $scope.concepts[0].id;
			}

			$scope.conceptSelected = function(conceptId, currentConceptId)
			{
				if (conceptId == currentConceptId) return true;
				return false;
			}

			$scope.isSelectedProperty = function(availableProperty, selectedProperty) {
				if (availableProperty == selectedProperty) return true;
				return false;
			}
		}
	}
};
