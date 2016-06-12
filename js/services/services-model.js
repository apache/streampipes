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
    	
    }).factory('measurementUnitsService', function($http, restApi){
    	var measurementUnitsService = {};
    	
    	var allMeasurementUnits = {};
    	var allMeasurementUnitTypes = {};
    	    	
    	var updateUnits = function() {
    		restApi.getAllUnits()
    		.success(function(measurementUnits){
                allMeasurementUnits = measurementUnits;
            })
            .error(function(msg){
                console.log(msg);
            });
    	};
        
    	
    	var updateUnitTypes = function() {
    		restApi.getAllUnitTypes()
    		.success(function(measurementUnits){
                allMeasurementUnitTypes = measurementUnits;
            })
            .error(function(msg){
                console.log(msg);
            });
    	};
        
    	updateUnits();
    	updateUnitTypes();
    	
    	measurementUnitsService.getUnits = function() {
    		return allMeasurementUnits;
    	}
    	
    	measurementUnitsService.getUnitTypes = function() {
    		return allMeasurementUnitTypes;
    	}
    	
    	measurementUnitsService.updateUnits = function() {
    		updateUnits();
    	}
    	
    	measurementUnitsService.updateUnitTypes = function() {
    		updateUnitTypes;
    	}
    	
    	return measurementUnitsService;
    	
    }).factory('deploymentService', function($http, $rootScope, restApi) {
    	
    	var deploymentService = {};
    	
    	deploymentService.updateElement = function(deploymentConfig, model) {
    		return $http({method: 'POST', 
    			headers: {'Accept' : 'application/json', 'Content-Type': undefined}, 
    			url: '/semantic-epa-backend/api/v2/users/' +$rootScope.email +'/deploy/update', 
    			data : getFormData(deploymentConfig, model)});
    	}
	
		deploymentService.generateImplementation = function(deploymentConfig, model) {			
			return $http({method: 'POST', 
				responseType : 'arraybuffer', 
				headers: {'Accept' : 'application/zip', 'Content-Type': undefined}, 
				url: '/semantic-epa-backend/api/v2/users/' +$rootScope.email +'/deploy/implementation', 
				data : getFormData(deploymentConfig, model)});
			}
		
		deploymentService.generateDescription = function(deploymentConfig, model) {
			return $http({method: 'POST', 
				responseType : 'arraybuffer', 
				headers: {'Accept' : 'application/json', 'Content-Type': undefined}, 
				url: '/semantic-epa-backend/api/v2/users/' +$rootScope.email +'/deploy/description', 
				data : getFormData(deploymentConfig, model)});
		}
		
		var getFormData = function(deploymentConfig, model) {
			var formData = new FormData();
			formData.append("config", angular.toJson(deploymentConfig));
			formData.append("model", angular.toJson(model));
			return formData;
		} 	
		
		return deploymentService;
    });