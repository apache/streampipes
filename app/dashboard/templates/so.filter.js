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


function nu() {

return function(eventProperties) {
		var result = [];
		angular.forEach(eventProperties, function(eventProperty) {
			if (eventProperty.properties.runtimeType.indexOf('http://www.w3.org/2001/XMLSchema#float') > -1) {
				result.push(eventProperty)
			}
			else if (eventProperty.properties.runtimeType.indexOf('http://www.w3.org/2001/XMLSchema#integer') > -1) {
				result.push(eventProperty)
			}
			else if (eventProperty.properties.runtimeType.indexOf('http://www.w3.org/2001/XMLSchema#double') > -1) {
				result.push(eventProperty)
			} else if (eventProperty.properties.domainProperties.indexOf('http://schema.org/Number') > -1) {
				result.push(eventProperty)
			}
		});
		return result;
	};

	//var result = [];

	//result.push(filter('http://schema.org/Number'))
	//result.push(runtimeTypeFilter('http://www.w3.org/2001/XMLSchema#float'))
	//result.push(runtimeTypeFilter('http://www.w3.org/2001/XMLSchema#integer'))
	//result.push(runtimeTypeFilter('http://www.w3.org/2001/XMLSchema#double'))



	//return uniqueArraybyId(result, runtimeName);
}

function geoLat() {
	return filter('http://www.w3.org/2003/01/geo/wgs84_pos#lat');
}

function geoLng() {
	return filter('http://www.w3.org/2003/01/geo/wgs84_pos#long');
}

function soNumber() {
	return filter('http://schema.org/Number');
};

function soDateTime() {
	return filter('http://schema.org/DateTime');
};

export default {
	nu: nu,
	soNumber: soNumber,
	soDateTime: soDateTime,
	geoLat: geoLat,
	geoLng: geoLng
}
