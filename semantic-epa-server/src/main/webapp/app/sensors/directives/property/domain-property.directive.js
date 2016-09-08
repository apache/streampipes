domainProperty.$inject = ['domainPropertiesService'];

export default function domainProperty(domainPropertiesService) {
	return {
		restrict : 'E',
		templateUrl : 'app/sensors/directives/property/domain-property.tmpl.html',
		scope : {
			property : "=",
			disabled : "=disabled"
		},
		controller: function($scope, $element) {
			$scope.domainProperties = [];
			$scope.domainProperties = domainPropertiesService.getDomainProperties();
		}
	}
};
