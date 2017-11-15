eventProperties.$inject = ['restApi'];

export default function eventProperties(restApi) {
	return {
		restrict: 'E',
		templateUrl: 'app/sensors/directives/property/event-properties.tmpl.html',
		scope : {
			properties : "=properties",
			disabled : "=disabled"
		},
		link: function($scope, element, attrs) {

			$scope.primitiveClasses = [{"title" : "String", "description" : "A textual datatype, e.g., 'machine1'", "id" : "http://www.w3.org/2001/XMLSchema#string"},
				{"title" : "Boolean", "description" : "A true/false value", "id" : "http://www.w3.org/2001/XMLSchema#boolean"},
				{"title" : "Integer", "description" : "A whole-numerical datatype, e.g., '1'", "id" : "http://www.w3.org/2001/XMLSchema#integer"},
				{"title" : "Double", "description" : "A floating-point number, e.g., '1.25'", "id" : "http://www.w3.org/2001/XMLSchema#double"}];    	

	$scope.existingProperties = [];

	$scope.loadProperties = function(){
		restApi.getOntologyProperties()
			.success(function(propertiesData){
				$scope.existingProperties = propertiesData;
			})
			.error(function(msg){
				console.log(msg);
			});
	};

	$scope.addProperty = function(properties) {
		if (properties == undefined) properties = [];
		properties.push({"type" : "org.streampipes.model.schema.EventPropertyPrimitive", "properties" : {"runtimeType" : "", "domainProperties" : [""]}});
	}
	$scope.loadProperties();
		}
	}
};
