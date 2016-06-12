angular
    .module('streamPipesApp')
     .directive('datatypeProperty', function() {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/datatype-property.tmpl.html',
    		scope : {
    			runtimeType : "=",
    			disabled : "=disabled",
    			dpMode : "=dpMode"
    		},
    		controller: function($scope, $element) {
    			
    			$scope.primitiveClasses = [{"title" : "String", "description" : "A textual datatype, e.g., 'machine1'", "id" : "http://www.w3.org/2001/XMLSchema#string"},
	        	                           {"title" : "Boolean", "description" : "A true/false value", "id" : "http://www.w3.org/2001/XMLSchema#boolean"},
	        	                           {"title" : "Integer", "description" : "A whole-numerical datatype, e.g., '1'", "id" : "http://www.w3.org/2001/XMLSchema#integer"},
	        	                           {"title" : "Long", "description" : "A whole numerical datatype, e.g., '2332313993'", "id" : "http://www.w3.org/2001/XMLSchema#long"},
	        	                           {"title" : "Double", "description" : "A floating-point number, e.g., '1.25'", "id" : "http://www.w3.org/2001/XMLSchema#double"},
	        	                           {"title" : "Float", "description" : "A floating-point number, e.g., '1.25'", "id" : "http://www.w3.org/2001/XMLSchema#float"}];    	

    			
    			if ($scope.dpMode == 'restriction')
    				$scope.primitiveClasses.push( {"title" : "Number", "description" : "Any numerical value", "id" : "http://schema.org/Number"});
    		}
    	}
    })
    .directive('domainProperty', function(restApi, domainPropertiesService) {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/domain-property.tmpl.html',
    		scope : {
    			property : "=",
    			disabled : "=disabled"
    		},
    		controller: function($scope, $element) {
    			
    			$scope.domainProperties = [];
    			$scope.domainProperties = domainPropertiesService.getDomainProperties();
	            
    		}
    	}
    }).directive('eventProperties', function(restApi) {
    	return {
	        restrict: 'E',
	        templateUrl: 'modules/sensors/directives/property.tmpl.html',
	        scope : {
	        	properties : "=properties",
    			disabled : "=disabled"
	        },
	        link: function($scope, element, attrs) {

	        	$scope.primitiveClasses = [{"title" : "String", "description" : "A textual datatype, e.g., 'machine1'", "id" : "http://www.w3.org/2001/XMLSchema#string"},
	        	                           {"title" : "Boolean", "description" : "A true/false value", "id" : "http://www.w3.org/2001/XMLSchema#boolean"},
	        	                           {"title" : "Integer", "description" : "A whole-numerical datatype, e.g., '1'", "id" : "http://www.w3.org/2001/XMLSchema#integer"},
	        	                           {"title" : "Double", "description" : "A floating-point number, e.g., '1.25'", "id" : "http://www.w3.org/2001/XMLSchema#double"}];    	

	        	$scope.existingProperties = [];
	        	
	        	$scope.loadProperties = function(){
	                restApi.getOntologyProperties()
	                    .success(function(propertiesData){
	                        $scope.existingProperties = propertiesData;
	                    })
	                    .error(function(msg){
	                        console.log(msg);
	                    });
	            };
	        	
	            $scope.addProperty = function(properties) {
	            	console.log(properties);
	    			if (properties == undefined) properties = [];
	    			 properties.push({"type" : "de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive", "properties" : {"runtimeType" : "", "domainProperties" : [""]}});
	    			 console.log("properties sie");
	    			 console.log($scope.properties.length);
	            }
	    		
	       
		    	$scope.loadProperties();
		    	
	        }
    	}
    	
    });