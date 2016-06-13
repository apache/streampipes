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
    }).directive('userAvatar', function() {
		return {
			replace: true,
			template: '<svg class="user-avatar" viewBox="0 0 128 128" height="64" width="64" pointer-events="none" display="block" > <path fill="#FF8A80" d="M0 0h128v128H0z"/> <path fill="#FFE0B2" d="M36.3 94.8c6.4 7.3 16.2 12.1 27.3 12.4 10.7-.3 20.3-4.7 26.7-11.6l.2.1c-17-13.3-12.9-23.4-8.5-28.6 1.3-1.2 2.8-2.5 4.4-3.9l13.1-11c1.5-1.2 2.6-3 2.9-5.1.6-4.4-2.5-8.4-6.9-9.1-1.5-.2-3 0-4.3.6-.3-1.3-.4-2.7-1.6-3.5-1.4-.9-2.8-1.7-4.2-2.5-7.1-3.9-14.9-6.6-23-7.9-5.4-.9-11-1.2-16.1.7-3.3 1.2-6.1 3.2-8.7 5.6-1.3 1.2-2.5 2.4-3.7 3.7l-1.8 1.9c-.3.3-.5.6-.8.8-.1.1-.2 0-.4.2.1.2.1.5.1.6-1-.3-2.1-.4-3.2-.2-4.4.6-7.5 4.7-6.9 9.1.3 2.1 1.3 3.8 2.8 5.1l11 9.3c1.8 1.5 3.3 3.8 4.6 5.7 1.5 2.3 2.8 4.9 3.5 7.6 1.7 6.8-.8 13.4-5.4 18.4-.5.6-1.1 1-1.4 1.7-.2.6-.4 1.3-.6 2-.4 1.5-.5 3.1-.3 4.6.4 3.1 1.8 6.1 4.1 8.2 3.3 3 8 4 12.4 4.5 5.2.6 10.5.7 15.7.2 4.5-.4 9.1-1.2 13-3.4 5.6-3.1 9.6-8.9 10.5-15.2M76.4 46c.9 0 1.6.7 1.6 1.6 0 .9-.7 1.6-1.6 1.6-.9 0-1.6-.7-1.6-1.6-.1-.9.7-1.6 1.6-1.6zm-25.7 0c.9 0 1.6.7 1.6 1.6 0 .9-.7 1.6-1.6 1.6-.9 0-1.6-.7-1.6-1.6-.1-.9.7-1.6 1.6-1.6z"/> <path fill="#E0F7FA" d="M105.3 106.1c-.9-1.3-1.3-1.9-1.3-1.9l-.2-.3c-.6-.9-1.2-1.7-1.9-2.4-3.2-3.5-7.3-5.4-11.4-5.7 0 0 .1 0 .1.1l-.2-.1c-6.4 6.9-16 11.3-26.7 11.6-11.2-.3-21.1-5.1-27.5-12.6-.1.2-.2.4-.2.5-3.1.9-6 2.7-8.4 5.4l-.2.2s-.5.6-1.5 1.7c-.9 1.1-2.2 2.6-3.7 4.5-3.1 3.9-7.2 9.5-11.7 16.6-.9 1.4-1.7 2.8-2.6 4.3h109.6c-3.4-7.1-6.5-12.8-8.9-16.9-1.5-2.2-2.6-3.8-3.3-5z"/> <circle fill="#444" cx="76.3" cy="47.5" r="2"/> <circle fill="#444" cx="50.7" cy="47.6" r="2"/> <path fill="#444" d="M48.1 27.4c4.5 5.9 15.5 12.1 42.4 8.4-2.2-6.9-6.8-12.6-12.6-16.4C95.1 20.9 92 10 92 10c-1.4 5.5-11.1 4.4-11.1 4.4H62.1c-1.7-.1-3.4 0-5.2.3-12.8 1.8-22.6 11.1-25.7 22.9 10.6-1.9 15.3-7.6 16.9-10.2z"/> </svg>'
		};
	})
	.directive('ngRightClick', function($parse) {
		return function(scope, element, attrs) {
			var fn = $parse(attrs.ngRightClick);
			element.bind('contextmenu', function(scope, event) {
				event.preventDefault();
				fn();

			});
		};
	});