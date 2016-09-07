sonumber.$inject = [];

export default function sonumber() {
	return function(eventProperties) {
		var result = [];
		angular.forEach(eventProperties, function(eventProperty) {
			if (eventProperty.properties.domainProperties.indexOf('http://schema.org/Number') > -1) {
				result.push(eventProperty)
			}
		});
		return result;
	};
};
