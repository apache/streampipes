deploymentService.$inject = ['$http', '$rootScope', 'RestApi'];

export default function deploymentService($http, $rootScope, RestApi) {

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

	deploymentService.generateDescriptionJava = function(deploymentConfig, model) {
		return $http({method: 'POST', 
			headers: {'Accept' : 'text/plain', 'Content-Type': undefined}, 
			url: '/semantic-epa-backend/api/v2/users/' +$rootScope.email +'/deploy/description/java', 
			data : getFormData(deploymentConfig, model)});
	}

	deploymentService.generateDescriptionJsonld = function(deploymentConfig, model) {
		return $http({method: 'POST', 
			headers: {'Accept' : 'application/json', 'Content-Type': undefined}, 
			url: '/semantic-epa-backend/api/v2/users/' +$rootScope.email +'/deploy/description/jsonld', 
			data : getFormData(deploymentConfig, model)});
	}

	var getFormData = function(deploymentConfig, model) {
		var formData = new FormData();
		formData.append("config", angular.toJson(deploymentConfig));
		formData.append("model", angular.toJson(model));
		return formData;
	} 	

	return deploymentService;
};
