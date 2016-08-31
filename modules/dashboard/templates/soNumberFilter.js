angular.
	module('streamPipesApp').
	filter('sonumber', function() {
		return function(eventProperties) {
			var result = [];
			angular.forEach(eventProperties, function(eventProperty) {
				if (eventProperty.properties.domainProperties.indexOf('http://schema.org/DateTime')) {
					result.push(eventProperty)
				}
			});
			return result;
		};
	});
