angular
    .module('streamPipesApp')
    .directive('sepaBasics', function() {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/basics.tmpl.html',
    		scope : {
    			element : "=element",
    			disabled : "=disabled"
    		}
    	}
    }) 
    .directive('requiredPropertyValues', function() {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/required-property-values.tmpl.html',
    		scope : {
    			property : "=",
    			disabled : "=disabled"
    		}
    	}
    }) 
    .directive('propertyRestriction', function(restApi) {
    	return {
	        restrict: 'E',
	        templateUrl: 'modules/sensors/directives/property-restriction.tmpl.html',
	        scope : {
	        	restriction : "=element",
    			disabled : "=disabled"
	        },
	        link: function($scope, element, attrs) {
	        	
	             	
	            $scope.addPropertyRestriction = function(key, restriction) {
	    			if (restriction.eventSchema.eventProperties == undefined) restriction.eventSchema.eventProperties = [];
	    			 restriction.eventSchema.eventProperties.push({"type" : "de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive", "properties" : {"elementName" : makeElementName(), "runtimeType" : "", "domainProperties" : []}});
	    		}
	    		
		    	 
		    	$scope.datatypeRestricted = function(property) {
		    		if (property.properties.runtimeType == undefined) return false;
		    		return true;
		    	};
		    	 
		    	$scope.toggleDatatypeRestriction = function(property) {
		    		 if ($scope.datatypeRestricted(property)) property.properties.runtimeType = undefined;
		    		 else property.properties.runtimeType = "";
		    	}
		    	
		    	$scope.measurementUnitRestricted = function(property) {
		    		if (property.properties.measurementUnit == undefined) return false;
		    		return true;
		    	};
		    	 
		    	$scope.toggleMeasurementUnitRestriction = function(property) {
		    		 if ($scope.measurementUnitRestricted(property)) property.properties.measurementUnit = undefined;
		    		 else property.properties.measurementUnit = "";
		    	}
		    	 
		    	$scope.domainPropertyRestricted = function(property) {
			    		if (property.properties.domainProperties == undefined) return false;
			    		if (property.properties.domainProperties[0] == undefined) return false;
			    		return true;
			    };
			    
			    $scope.toggleDomainPropertyRestriction = function(property) {
		    		 if ($scope.domainPropertyRestricted(property))
	    			 {
	    			 	property.properties.domainProperties = [];
	    			 }
		    		 else 
	    			 {
		    			 property.properties.domainProperties = [];
		    			 property.properties.domainProperties[0] = "";
	    			 }
		    	}
			    
			    var makeElementName = function() {
			    	return "urn:fzi.de:sepa:" +randomString();
			    }
			    
			    var randomString = function() {
			        var result = '';
			        var chars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
			        for (var i = 0; i < 12; i++) result += chars[Math.round(Math.random() * (chars.length - 1))];
			        return result;
			    };
			    
			  
	        }
    	}
    	
    }).directive('streamRestriction', function() {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/stream-restriction.tmpl.html',
    		scope : {
    			streams : "=element",
    			disabled : "=disabled"
    		},
    		link: function($scope, element, attrs) {

	            $scope.addStreamRestriction = function(streams) {
	    			if (streams == undefined) streams = [];
	    			streams.push({"eventSchema" : {"eventProperties" : []}});
	    		}
	    		
		    	$scope.removeStreamRestriction = function(streamIndex, streams) {
		    		   streams.splice(streamIndex, 1);
		    	};
    		}
    	}
    }).directive('options', function() {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/options.tmpl.html',
    		scope : {
    			options : "=element",
    			disabled : "=disabled"
    		},
    		link: function($scope, element, attrs) {
    			  			
	            $scope.addOption = function(options) {   
	    			if (options == undefined) options = [];
	    			 options.push({"name" : ""});
	    		}
	    		
		    	$scope.removeOption = function(options, index) {
		    		   options.splice(index, 1);
		    	};
    		}		
    	}
    }).directive('outputStrategy', function() {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/output-strategy.tmpl.html',
    		scope : {
    			strategies : "=strategies",
    			disabled : "=disabled"
    		},
    		link: function($scope, element, attrs) {
    			  			
    			$scope.outputStrategyTypes = [{label : "Append", "type" : "de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy"},
    				                           {label : "Custom", "type" : "de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy"},
    				                           {label : "Fixed", "type" : "de.fzi.cep.sepa.model.impl.output.FixedOutputStrategy"},
    				                           {label : "List", "type" : "de.fzi.cep.sepa.model.impl.output.ListOutputStrategy"},
    				                           {label : "Keep", "type" : "de.fzi.cep.sepa.model.impl.output.RenameOutputStrategy"}];

    			$scope.selectedOutputStrategy = $scope.outputStrategyTypes[0].type;
    			
	            $scope.addOutputStrategy = function(strategies) {   
	            	 if (strategies == undefined) $scope.strategies = [];
	    			 $scope.strategies.push(getNewOutputStrategy());
	    		}
	    			 
		    	$scope.removeOutputStrategy = function(strategies, index) {
		    		   strategies.splice(index, 1);
		    	};
		    	
		    	var getNewOutputStrategy = function() {
	            	if ($scope.selectedOutputStrategy === $scope.outputStrategyTypes[0].type)
	            		return {"type" : $scope.outputStrategyTypes[0].type, "properties" : {"eventProperties" : []}};
	            	else if ($scope.selectedOutputStrategy === $scope.outputStrategyTypes[1].type)
		            	return {"type" : $scope.outputStrategyTypes[1].type, "properties" : {"eventProperties" : []}};
		            else if ($scope.selectedOutputStrategy === $scope.outputStrategyTypes[2].type)
	            		return {"type" : $scope.outputStrategyTypes[2].type, "properties" : {"eventProperties" : []}};
	            	else if ($scope.selectedOutputStrategy === $scope.outputStrategyTypes[3].type)
		            	return {"type" : $scope.outputStrategyTypes[3].type, "properties" : {}};
		            else if ($scope.selectedOutputStrategy === $scope.outputStrategyTypes[4].type)
			            return {"type" : $scope.outputStrategyTypes[4].type, "properties" : {}};
	            	 
	            }
		    	
		    	$scope.getType = function(strategy) {
		    		var label;
		    		angular.forEach($scope.outputStrategyTypes, function(value) {
		    			if (value.type == strategy.type) label = value.label;
		    		});
		    		return label;
		    	};
    		}		
    	}
    }).directive('multipleValueInput', function() {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/editor/directives/multiple-value-input.tmpl.html',
    		scope : {
    			staticProperty : "=",
    		},
    		 link: function($scope, element, attrs) {
    			 
    			 $scope.addTextInputRow = function(members) {
    				 members.push({"input" : {"type" : "TextInput", "properties" : {"description" : "", "value" : ""}}});
    			 }
    	
    			 $scope.removeTextInputRow = function(members, property) {
    				 members.splice(property, 1);
    			 }
    			 
    			 $scope.addDomainConceptRow = function(firstMember, members) {
    				 var supportedProperties = [];
    				 angular.forEach(firstMember.input.properties.supportedProperties, function(property) {
    					 supportedProperties.push({"propertyId" : property.propertyId, "value" : ""});
    				 });
    				 members.push({"input" : {"type" : "DomainConceptInput", "properties" : {"elementType" : "DOMAIN_CONCEPT", "description" : "", "supportedProperties" : supportedProperties, "requiredClass" : firstMember.input.properties.requiredClass}}});
    			 }
    	
    			 $scope.removeDomainConceptRow = function(members, property) {
    				 members.splice(property, 1);
    			 }
    		 }
    	}
    });