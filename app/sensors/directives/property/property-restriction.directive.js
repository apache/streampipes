propertyRestriction.$inject = [];

export default function propertyRestriction() {
	return {
		restrict: 'E',
		templateUrl: 'app/sensors/directives/property/property-restriction.tmpl.html',
		scope : {
			restriction : "=element",
			disabled : "=disabled"
		},
		link: function($scope, element, attrs) {


			$scope.addPropertyRestriction = function(key, restriction) {
				if (restriction.eventSchema.eventProperties == undefined) restriction.eventSchema.eventProperties = [];
				restriction.eventSchema.eventProperties.push({"type" : "org.streampipes.model.schema.EventPropertyPrimitive", "properties" : {"elementName" : makeElementName(), "runtimeType" : "", "domainProperties" : []}});
			}


			$scope.datatypeRestricted = function(property) {
				if (property.properties.runtimeType == undefined) return false;
				return true;
			};

			$scope.toggleDatatypeRestriction = function(property) {
				if ($scope.datatypeRestricted(property)) property.properties.runtimeType = undefined;
				else property.properties.runtimeType = "";
			}

			$scope.measurementUnitRestricted = function(property) {
				if (property.properties.measurementUnit == undefined) return false;
				return true;
			};

			$scope.toggleMeasurementUnitRestriction = function(property) {
				if ($scope.measurementUnitRestricted(property)) property.properties.measurementUnit = undefined;
				else property.properties.measurementUnit = "";
			}

			$scope.domainPropertyRestricted = function(property) {
				if (property.properties.domainProperties == undefined) return false;
				if (property.properties.domainProperties[0] == undefined) return false;
				return true;
			};

			$scope.toggleDomainPropertyRestriction = function(property) {
				if ($scope.domainPropertyRestricted(property))
				{
					property.properties.domainProperties = [];
				}
				else 
				{
					property.properties.domainProperties = [];
					property.properties.domainProperties[0] = "";
				}
			}

			var makeElementName = function() {
				return "urn:fzi.de:sepa:" +randomString();
			}

			var randomString = function() {
				var result = '';
				var chars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
				for (var i = 0; i < 12; i++) result += chars[Math.round(Math.random() * (chars.length - 1))];
				return result;
			};


		}
	}
};
