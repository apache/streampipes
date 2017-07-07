staticProperties.$inject = ['restApi'];

export default function staticProperties(restApi) {
	return {
		restrict : 'E',
		templateUrl : 'app/sensors/directives/property/static-properties.tmpl.html',
		scope : {
			staticProperties : "=element",
			streams : '=',
			disabled : "=disabled"
		},
		controller: function($scope, $element) {
			$scope.range = function(count){
				return new Array(+count);
			};
			$scope.staticPropertyTypes = [{label : "Text Input", "type" : "org.streampipes.model.impl.staticproperty.FreeTextStaticProperty"},
				{label : "Single-Value Selection", "type" : "org.streampipes.model.impl.staticproperty.OneOfStaticProperty"},
				{label : "Multi-Value Selection", "type" : "org.streampipes.model.impl.staticproperty.AnyStaticProperty"},
				{label : "Domain Concept", "type" : "org.streampipes.model.impl.staticproperty.DomainStaticProperty"},
				{label : "Single-Value Mapping Property", "type" : "org.streampipes.model.impl.staticproperty.MappingPropertyUnary"},
				{label : "Multi-Value Mapping Property", "type" : "org.streampipes.model.impl.staticproperty.MappingPropertyNary"},
				{label : "Collection", "type" : "org.streampipes.model.impl.staticproperty.CollectionStaticProperty"}];

			$scope.newStaticPropertyType = $scope.staticPropertyTypes[0].type;
			$scope.memberTypeSelected = false;


			$scope.isSelectedProperty = function(mapsFrom, property) {
				if (property.properties.elementName == mapsFrom) return true;
				return false;
			}; 			

			$scope.addStaticProperty = function(staticProperties, type) {
				if (staticProperties == undefined) staticProperties = [];
				staticProperties.push($scope.getNewStaticProperty(type));
			}

			$scope.getNewStaticProperty = function(type) {
				if (type === $scope.staticPropertyTypes[0].type)
					return {"type" : $scope.staticPropertyTypes[0].type, "properties" : {"label" : "", "description" : ""}};
				else if (type === $scope.staticPropertyTypes[1].type)
					return {"type" : $scope.staticPropertyTypes[1].type, "properties" : {"label" : "", "description" : "", "options" : []}};
				else if (type === $scope.staticPropertyTypes[2].type)
					return {"type" : $scope.staticPropertyTypes[2].type, "properties" : {"label" : "", "description" : "", "options" : []}};
				else if (type === $scope.staticPropertyTypes[3].type)
					return {"type" : $scope.staticPropertyTypes[3].type, "properties" : {"label" : "", "description" : "", "supportedProperties" : []}};
				else if (type === $scope.staticPropertyTypes[4].type)
					return {"type" : $scope.staticPropertyTypes[4].type, "properties" : {"label" : "", "description" : ""}};
				else if (type === $scope.staticPropertyTypes[5].type)
					return {"type" : $scope.staticPropertyTypes[5].type, "properties" : {"label" : "", "description" : ""}};
				else if (type === $scope.staticPropertyTypes[6].type)
					return {"type" : $scope.staticPropertyTypes[6].type, "properties" : {"label" : "", "description" : "", "memberType" : "", "members" : []}};  
			}

			$scope.getType = function(property) {
				var label;
				angular.forEach($scope.staticPropertyTypes, function(value) {
					if (value.type == property.type) label = value.label;
				});
				return label;
			};

			$scope.domainPropertyRestricted = function(property) {
				if (property.type == undefined) return false;
				return true;
			};

			$scope.toggleDomainPropertyRestriction = function(property) {
				if (property.type != undefined) property.type = undefined;
				else property.type = $scope.properties[0].id;
			}

			$scope.addMember = function(property) {
				property.members.push(angular.copy($scope.getNewStaticProperty(property.memberType)));
				$scope.memberTypeSelected = true;
			}

			$scope.removeMember = function(property) {
				property.members = [];
				property.memberType = '';
				$scope.memberTypeSelected = false;
			}
		},
		link: function($scope, element, attrs) {	

			$scope.properties = [];

			$scope.loadProperties = function(){
				restApi.getOntologyProperties()
					.success(function(propertiesData){
						$scope.properties = propertiesData;
					})
					.error(function(msg){
						console.log(msg);
					});
			};

			$scope.loadProperties();
		}
	}
};
