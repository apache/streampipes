import restApi from './rest-api.service'

domainProperties.$inject = ['$http', 'restApi'];

export default function domainProperties($http, restApi){
	var domainPropertiesService = {};

	var availableDomainProperties;

	restApi.getOntologyProperties()
		.success(function(propertiesData){
			availableDomainProperties = propertiesData;
		})
		.error(function(msg){
			console.log(msg);
		});

	domainPropertiesService.getDomainProperties = function() {
		return availableDomainProperties;
	}

	return domainPropertiesService;

};
