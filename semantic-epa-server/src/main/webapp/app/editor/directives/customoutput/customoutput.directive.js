customOutput.$inject = [];

export default function customOutput() {
	return {
		restrict : 'E',
		templateUrl : 'app/editor/directives/customoutput/customoutput.tmpl.html',
		scope : {
			outputStrategy : "="
		},
		link: function (scope) {
			scope.toggle = function(property) {
				if (scope.exists(property)) {
					remove(property);
				} else {
					add(property);
				}
			}

			scope.exists = function(property) {
				if (!scope.outputStrategy.properties.eventProperties) {
					return false;
				} else {
					var valid = false;
					angular.forEach(scope.outputStrategy.properties.eventProperties, function(p) {
						if (property.properties.elementId === p.properties.elementId) {
							valid = true;
						}
					});
					return valid;
				}
			}

			var add = function(property) {
				if (!scope.outputStrategy.properties.eventProperties) {
					scope.outputStrategy.properties.eventProperties = [];
				}
				scope.outputStrategy.properties.eventProperties.push(property);

			}

			var remove = function(property) {
				var index = scope.outputStrategy.properties.eventProperties.indexOf(property);
				scope.outputStrategy.properties.eventProperties.splice(index, 1);
			}

		}
	}

};
