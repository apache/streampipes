function filter(soType) {
	return function(eventProperties) {
		var result = [];
		angular.forEach(eventProperties, function(eventProperty) {
			if (eventProperty.properties.domainProperties.indexOf(soType) > -1) {
				result.push(eventProperty)
			}
		});
		return result;
	};
}

function soNumber() {
	return filter('http://schema.org/Number');
};

function soDateTime() {
	return filter('http://schema.org/DateTime');
};

export default {
	soNumber: soNumber,
	soDateTime: soDateTime
}
