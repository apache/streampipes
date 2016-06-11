angular
    .module('streamPipesApp')
    .directive('propertyQualityDefinitions', function(restApi, measurementUnitsService, $q, $log) {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/property-quality-definitions.tmpl.html',
    		scope : {
    			disabled : "=",
    			property : "=",
    			runtimeType :"="
    		},
    		controller: function($scope, $element)  {
    			
    			$scope.qualities = [{label : "Accuracy", "description" : "", "type" : "de.fzi.cep.sepa.model.impl.quality.Accuracy"},
    			                    {label : "Precision", "description" : "", "type" : "de.fzi.cep.sepa.model.impl.quality.Precision"},
    			                    {label : "Resolution", "description" : "", "type" : "de.fzi.cep.sepa.model.impl.quality.Resolution"}];

    				
    			
    			$scope.add = function() {
	    			if ($scope.property.properties == undefined) {
	    				$scope.property.properties = {};
	    				$scope.property.properties.runtimeValues = [];
	    			}
	    			$scope.property.properties.runtimeValues.push("");
	    		}
	    		
		    	$scope.remove = function(runtimeValues, propertyIndex) {
		    		   runtimeValues.splice(propertyIndex, 1);
		    	};
    		 }
    	}
    }).directive('streamQualityDefinitions', function(restApi, measurementUnitsService, $q, $log) {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/stream-quality-definitions.tmpl.html',
    		scope : {
    			disabled : "=",
    			property : "=",
    			runtimeType :"="
    		},
    		controller: function($scope, $element)  {
    			
    			$scope.qualities = [{label : "Frequency", "description" : "", "type" : "de.fzi.cep.sepa.model.impl.quality.Frequency"},
    			                    {label : "Latency", "description" : "", "type" : "de.fzi.cep.sepa.model.impl.quality.Latency"}];

    				
    			
    			$scope.add = function() {
	    			if ($scope.property.properties == undefined) {
	    				$scope.property.properties = {};
	    				$scope.property.properties.runtimeValues = [];
	    			}
	    			$scope.property.properties.runtimeValues.push("");
	    		}
	    		
		    	$scope.remove = function(runtimeValues, propertyIndex) {
		    		   runtimeValues.splice(propertyIndex, 1);
		    	};
    		 }
    	}
    });