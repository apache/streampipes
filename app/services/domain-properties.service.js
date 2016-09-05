//TODO
import angular from 'angular';
angular
    .module('streamPipesApp')
    .factory('domainPropertiesService', function($http, restApi){
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
    	
    })
