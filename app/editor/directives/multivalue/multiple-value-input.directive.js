multipleValueInput.$inject = [];

export default function multipleValueInput() {
	return {
		restrict : 'E',
		templateUrl : './multiple-value-input.tmpl.html',
		scope : {
			staticProperty : "=",
		},
		link: function($scope, element, attrs) {

			$scope.addTextInputRow = function(members) {
				members.push({"input" : {"type" : "TextInput", "properties" : {"description" : "", "value" : ""}}});
			}

			$scope.removeTextInputRow = function(members, property) {
				members.splice(property, 1);
			}

			$scope.addDomainConceptRow = function(firstMember, members) {
				var supportedProperties = [];
				angular.forEach(firstMember.input.properties.supportedProperties, function(property) {
					supportedProperties.push({"propertyId" : property.propertyId, "value" : ""});
				});
				members.push({"input" : {"type" : "DomainConceptInput", "properties" : {"elementType" : "DOMAIN_CONCEPT", "description" : "", "supportedProperties" : supportedProperties, "requiredClass" : firstMember.input.properties.requiredClass}}});
			}

			$scope.removeDomainConceptRow = function(members, property) {
				members.splice(property, 1);
			}
		}
	}
};
