import RestApi from './rest-api.service'

domainProperties.$inject = ['$http', 'RestApi'];

export default function domainProperties($http, RestApi){
	var domainPropertiesService = {};

	var availableDomainProperties;

	RestApi.getOntologyProperties()
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
