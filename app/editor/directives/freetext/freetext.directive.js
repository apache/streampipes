freetext.$inject = [];

export default function freetext() {

	return {
		restrict: 'E',
		templateUrl: 'app/editor/directives/freetext/freetext.tmpl.html',
		scope: {
			staticProperty: "=",
			inputStreams : "=",
			mappingProperty: "="
		},
		link: function (scope) {
			if (scope.staticProperty.properties.valueSpecification) {
				scope.staticProperty.properties.value = (scope.staticProperty.properties.value*1);
			}

			scope.updateCurrentEventProperty = function(mapsTo) {
				var eventProperty;
				angular.forEach(scope.inputStreams, function (stream) {
					console.log(stream);
					angular.forEach(stream.eventSchema.eventProperties, function(property) {
						console.log(scope.mappingProperty);
						if (scope.mappingProperty.properties.mapsTo == property.properties.elementId) {
							console.log("elementId");
							console.log(property);
							eventProperty = property;
						}
					});
				});
				return eventProperty;
			}

			scope.$watchCollection('mappingProperty.properties', function(newValue, oldValue) {
				//console.log(scope.mappingProperty);
				//console.log(scope.mappingProperty.properties.mapsTo);
				scope.selectedEventProperty = scope.updateCurrentEventProperty(scope.mappingProperty.properties.mapsTo);
			});

			scope.selectedEventProperty = scope.updateCurrentEventProperty(scope.mappingProperty.properties.mapsTo);
		}
	}

};
