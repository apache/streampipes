import * as angular from 'angular';

function filter(soType) {
	return function(eventProperties) {
		var result = [];
		angular.forEach(eventProperties, function(eventProperty) {
			if (eventProperty.properties.domainProperties && eventProperty.properties.domainProperties.indexOf(soType) > -1) {
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
			if (eventProperty.properties == 'http://www.w3.org/2001/XMLSchema#float') {
				result.push(eventProperty)
			}
			else if (eventProperty.properties.runtimeType == 'http://www.w3.org/2001/XMLSchema#integer') {
				result.push(eventProperty)
			}
			else if (eventProperty.properties.runtimeType == 'http://www.w3.org/2001/XMLSchema#double') {
				result.push(eventProperty)
			} else if (eventProperty.properties.domainProperties && eventProperty.properties.domainProperties.indexOf('http://schema.org/Number') > -1) {
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

function image() {
	return filter("https://image.com");
}

function soDateTime() {
	return filter('http://schema.org/DateTime');
};

export default {
	nu: nu,
	soNumber: soNumber,
	soDateTime: soDateTime,
	geoLat: geoLat,
	geoLng: geoLng,
	image: image
}
