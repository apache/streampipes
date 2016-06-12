angular
    .module('streamPipesApp')
    .directive('deploymentType', function(restApi, measurementUnitsService, $q, $log) {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/deployment-type.tmpl.html',
    		scope : {
    			disabled : "=",
    			deployment : "="
    		},
    		controller: function($scope, $element) {
    					
    			
    		}
    	}
    }).directive('sinkDeployment', function(restApi, measurementUnitsService, $q, $log) {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/sink-deployment.tmpl.html',
    		scope : {
    			disabled : "=",
    			element : "="
    		},
    		controller: function($scope, $element) {
    					
    			
    		}
    	}
    }).directive('streamDeployment', function() {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/stream-deployment.tmpl.html',
    		scope : {
    			element : "=",
    			disabled : "=disabled"
    		},
    		
    		controller: function($scope, $element) {
    			
    			
    		}
    	}
    }).directive('sepaDeployment', function(restApi, measurementUnitsService, $q, $log) {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/sepa-deployment.tmpl.html',
    		scope : {
    			disabled : "=",
    			element : "="
    		},
    		controller: function($scope, $element) {
    					
    			
    		}
    	}
    });
        		