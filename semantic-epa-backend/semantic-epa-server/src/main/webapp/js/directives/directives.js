angular
    .module('streamPipesDirectives', ['ngMaterial'])
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
	        	
	        	$scope.primitiveClasses = [{"title" : "String", "description" : "A textual datatype, e.g., 'machine1'", "id" : "http://www.w3.org/2001/XMLSchema#string"},
	        	                           {"title" : "Boolean", "description" : "A true/false value", "id" : "http://www.w3.org/2001/XMLSchema#boolean"},
	        	                           {"title" : "Integer", "description" : "A whole-numerical datatype, e.g., '1'", "id" : "http://www.w3.org/2001/XMLSchema#integer"},
	        	                           {"title" : "Double", "description" : "A floating-point number, e.g., '1.25'", "id" : "http://www.w3.org/2001/XMLSchema#double"}];    	

	        	$scope.properties = [];
	        	
	        	$scope.loadProperties = function(){
	                restApi.getOntologyProperties()
	                    .success(function(propertiesData){
	                        $scope.properties = propertiesData;
	                    })
	                    .error(function(msg){
	                        console.log(msg);
	                    });
	            };
	            
	        	
	            $scope.addPropertyRestriction = function(key, restriction) {
	    			if (restriction.eventSchema.eventProperties == undefined) restriction.eventSchema.eventProperties = [];
	    			 restriction.eventSchema.eventProperties.push({"type" : "de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive", "properties" : {"elementName" : makeElementName(), "runtimeType" : "", "domainProperties" : []}});
	    		}
	    		
		    	$scope.removePropertyRestriction = function(propertyIndex, restriction) {
		    		   restriction.eventSchema.eventProperties.splice(propertyIndex, 1);
		    	};
		    	 
		    	$scope.datatypeRestricted = function(property) {
		    		if (property.properties.runtimeType == undefined) return false;
		    		return true;
		    	};
		    	 
		    	$scope.toggleDatatypeRestriction = function(property) {
		    		 if ($scope.datatypeRestricted(property)) property.properties.runtimeType = undefined;
		    		 else property.properties.runtimeType = "";
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
			    
			    $scope.loadProperties();
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
    }).directive('staticProperties', function(restApi) {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/sensors/directives/static-properties.tmpl.html',
    		scope : {
    			staticProperties : "=element",
    			streams : '=',
    			disabled : "=disabled"
    		},
    		controller: function($scope, $element) {
    			
    			$scope.staticPropertyTypes = [{label : "Text Input", "type" : "de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty"},
   				                           {label : "Single-Value Selection", "type" : "de.fzi.cep.sepa.model.impl.staticproperty.OneOfStaticProperty"},
   				                           {label : "Multi-Value Selection", "type" : "de.fzi.cep.sepa.model.impl.staticproperty.AnyStaticProperty"},
   				                           {label : "Domain Concept", "type" : "de.fzi.cep.sepa.model.impl.staticproperty.DomainStaticProperty"},
   				                           {label : "Single-Value Mapping Property", "type" : "de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyUnary"},
   				                           {label : "Multi-Value Mapping Property", "type" : "de.fzi.cep.sepa.model.impl.staticproperty.MappingPropertyNary"},
   				                           {label : "Collection", "type" : "de.fzi.cep.sepa.model.impl.staticproperty.CollectionStaticProperty"}];

    			$scope.newStaticPropertyType = $scope.staticPropertyTypes[0].type;
    			$scope.memberTypeSelected = false;
    			
    			
    			$scope.isSelectedProperty = function(mapsFrom, property) {
    				if (property.properties.elementName == mapsFrom) return true;
    				return false;
    			}; 			
    			
	            $scope.addStaticProperty = function(staticProperties, type) {
	    			if (staticProperties == undefined) staticProperties = [];
	    			 staticProperties.push($scope.getNewStaticProperty(type));
	    		}
	            
	            $scope.getNewStaticProperty = function(type) {
	            	if (type === $scope.staticPropertyTypes[0].type)
	            		return {"type" : $scope.staticPropertyTypes[0].type, "properties" : {"label" : "", "description" : ""}};
	            	else if (type === $scope.staticPropertyTypes[1].type)
		            	return {"type" : $scope.staticPropertyTypes[1].type, "properties" : {"label" : "", "description" : "", "options" : []}};
		            else if (type === $scope.staticPropertyTypes[2].type)
	            		return {"type" : $scope.staticPropertyTypes[2].type, "properties" : {"label" : "", "description" : "", "options" : []}};
	            	else if (type === $scope.staticPropertyTypes[3].type)
		            	return {"type" : $scope.staticPropertyTypes[3].type, "properties" : {"label" : "", "description" : "", "supportedProperties" : []}};
		            else if (type === $scope.staticPropertyTypes[4].type)
		            	return {"type" : $scope.staticPropertyTypes[4].type, "properties" : {"label" : "", "description" : ""}};
		            else if (type === $scope.staticPropertyTypes[5].type)
		            	return {"type" : $scope.staticPropertyTypes[5].type, "properties" : {"label" : "", "description" : ""}};
		            else if (type === $scope.staticPropertyTypes[6].type)
			            return {"type" : $scope.staticPropertyTypes[6].type, "properties" : {"label" : "", "description" : "", "memberType" : "", "members" : []}};  
	            }
	    		
		    	$scope.removeStaticProperty = function(staticProperties, staticPropertyIndex) {
		    		   staticProperties.splice(staticPropertyIndex, 1);
		    	};
		    	
		    	$scope.getType = function(property) {
		    		var label;
		    		angular.forEach($scope.staticPropertyTypes, function(value) {
		    			if (value.type == property.type) label = value.label;
		    		});
		    		return label;
		    	};
		    	
		    	$scope.domainPropertyRestricted = function(property) {
		    		if (property.type == undefined) return false;
		    		return true;
		    	};
		    
		    	$scope.toggleDomainPropertyRestriction = function(property) {
			    	if (property.type != undefined) property.type = undefined;
			    	else property.type = $scope.properties[0].id;
		    	}
		    	
		    	$scope.addMember = function(property) {
		    		property.members.push(angular.copy($scope.getNewStaticProperty(property.memberType)));
		    		$scope.memberTypeSelected = true;
		    	}
		    	
		    	$scope.removeMember = function(property) {
		    		property.members = [];
		    		property.memberType = '';
		    		$scope.memberTypeSelected = false;
		    	}
    		},
    		link: function($scope, element, attrs) {	
    			
    			$scope.properties = [];
	
	        	$scope.loadProperties = function(){
	                restApi.getOntologyProperties()
	                    .success(function(propertiesData){
	                        $scope.properties = propertiesData;
	                    })
	                    .error(function(msg){
	                        console.log(msg);
	                    });
	            };
		    	
		    	$scope.loadProperties();
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
    }).directive('domainConceptProperty', function(restApi) {
    	return {
    		restrict : 'AE',
    		templateUrl : 'modules/sensors/directives/domain-concept-property.tmpl.html',
    		scope : {
    			domainProperty : "=domainProperty",
    			disabled : "=disabled"
    		},
    		link: function(scope, element, attrs) {
    			  		
    			scope.concepts = [];
    			scope.properties = [];
    			    			
    			scope.loadProperties = function(){
	                restApi.getOntologyProperties()
	                    .success(function(propertiesData){
	                        scope.properties = propertiesData;
	                    })
	                    .error(function(msg){
	                        console.log(msg);
	                    });
	            };
    			
	            scope.loadConcepts = function(){
	                restApi.getOntologyConcepts()
	                    .success(function(conceptsData){
	                        scope.concepts = conceptsData;
	                    })
	                    .error(function(msg){
	                        console.log(msg);
	                    });
	            };
		    	
		    	scope.loadProperties();
		    	scope.loadConcepts();
		    	
		    	
    		},
    		controller: function($scope, $element) {
    			
    			$scope.addSupportedProperty = function(supportedProperties) {   
	    			if (supportedProperties == undefined) supportedProperties = [];
	    			 supportedProperties.push({"propertyId" : ""});
	    		}
	            
	            $scope.removeSupportedProperty = function(supportedProperties, index) {   	
	    			 supportedProperties.splice(index, 1);
	            }		
	            
	            $scope.conceptRestricted = function(domainProperty) {
		    		if (domainProperty.requiredClass == undefined) return false;
		    		return true;
		    	};
		    
		    	$scope.toggleConceptRestriction = function(domainProperty) {
			    	if ($scope.conceptRestricted(domainProperty)) domainProperty.requiredClass = undefined;
			    	else domainProperty.requiredClass = $scope.concepts[0].id;
		    	}
		    	
		    	$scope.conceptSelected = function(conceptId, currentConceptId)
		    	{
		    		if (conceptId == currentConceptId) return true;
		    		return false;
		    	}
		    	
		    	$scope.isSelectedProperty = function(availableProperty, selectedProperty) {
   				 if (availableProperty == selectedProperty) return true;
   				 return false;
   			 }
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
	    			if (properties == undefined) $scope.properties.eventProperties = [];
	    			 $scope.properties.eventProperties.push({"type" : "de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive", "properties" : {"runtimeType" : "", "domainProperties" : [""]}});
	    		}
	    		
		    	$scope.removeProperty = function(properties, propertyIndex) {
		    		   properties.splice(propertyIndex, 1);
		    	};
		    	
		    	$scope.loadProperties();
		    	
	        }
    	}
    	
    }).directive('supportedGrounding', function() {
    	return {
	        restrict: 'E',
	        templateUrl: 'modules/sensors/directives/supported-grounding.tmpl.html',
	        scope : {
	        	grounding : "=grounding",
    			disabled : "=disabled"
	        },
	        link: function($scope, element, attrs) {

	        	
	        	var transportFormatTypesThrift = ["http://sepa.event-processing.org/sepa#TransportFormat", "http://www.w3.org/2000/01/rdf-schema#Resource", "http://sepa.event-processing.org/sepa#thrift"];
	        	var transportFormatTypesJson = ["http://sepa.event-processing.org/sepa#TransportFormat", "http://www.w3.org/2000/01/rdf-schema#Resource", "http://sepa.event-processing.org/sepa#json"];
	        	
	        	var thriftFormat = "http://sepa.event-processing.org/sepa#thrift";
	        	var jsonFormat = "http://sepa.event-processing.org/sepa#json";
	        	
	        	$scope.kafkaClass = "de.fzi.cep.sepa.model.impl.KafkaTransportProtocol";
	        	$scope.jmsClass = "de.fzi.cep.sepa.model.impl.JmsTransportProtocol";
	        	   	
	            $scope.kafkaSelected = function(transportProtocols)
	            {
	            	return brokerSelected(transportProtocols, $scope.kafkaClass);
	            }
	            
	            $scope.jmsSelected = function(transportProtocols)
	            {
	            	return brokerSelected(transportProtocols, $scope.jmsClass);
	            }
	            
	            $scope.jsonSelected = function(transportFormats)
	            {
	            	return formatSelected(transportFormats, jsonFormat);
	            }
	            
	            $scope.thriftSelected = function(transportFormats)
	            {
	            	return formatSelected(transportFormats, thriftFormat);
	            }
	            
	            var brokerSelected = function(transportProtocols, protocolClass) {
	            	var selected = false;
	            	angular.forEach(transportProtocols, function(protocol) {
	            		if (protocol.type == protocolClass) selected = true;
	            	});
	            	return selected;
	            }
	            
	            var formatSelected = function(transportFormats, formatClass) {
	            	var selected = false;
	            	angular.forEach(transportFormats, function(format) {
	            		angular.forEach(format.rdfType, function(type) {
	            			if (type == formatClass) selected = true;
	            		})
	            	});
	            	return selected;
	            }
	            
	            $scope.toggleKafka = function(transportProtocols)
	            {
	            	if ($scope.kafkaSelected(transportProtocols)) {
	            		removeBroker(transportProtocols, $scope.kafkaClass);
	            	} 
	            	else {
	            		checkGrounding();
	            		if (transportProtocols == undefined) $scope.grounding.transportProtocols = [];
	            		$scope.grounding.transportProtocols.push({"type" : $scope.kafkaClass, "properties" : {"kafkaPort" : 0, "zookeeperPort" : 0}})
	            	}
	            }
	            
	            $scope.toggleJms = function(transportProtocols)
	            {
	            	if ($scope.jmsSelected(transportProtocols)) {
	            		removeBroker(transportProtocols, $scope.jmsClass);
	            	} 
	            	else {
	            		checkGrounding();
	            		if (transportProtocols == undefined) $scope.grounding.transportProtocols = [];
	            		$scope.grounding.transportProtocols.push({"type" : $scope.jmsClass, "properties" : {"port" : 0}})
	            	}
	            }
	            
	            var removeBroker = function(transportProtocols, protocolClass) {
	            	angular.forEach(transportProtocols, function(protocol) {
            			if (protocol.type == protocolClass) transportProtocols.splice(transportProtocols.indexOf(protocol), 1);
            		});
	            }
	            
	            $scope.toggleJson = function(transportFormats)
	            {
	            	if ($scope.jsonSelected(transportFormats)) {
	            		removeFormat(transportFormats, jsonFormat);
	            	}
	            	else {
	            		checkGrounding();
	            		if (transportFormats == undefined) $scope.grounding.transportFormats = [];
	            		$scope.grounding.transportFormats.push({"rdfType" : transportFormatTypesJson});
	            	}
	            }
	            
	            $scope.toggleThrift = function(transportFormats)
	            {
	            	if ($scope.thriftSelected(transportFormats)) {
	            		removeFormat(transportFormats, thriftFormat);
	            	}
	            	else {
	            		checkGrounding();
	            		if (transportFormats == undefined) $scope.grounding.transportFormats = [];
	            		$scope.grounding.transportFormats.push({"rdfType" : transportFormatTypesThrift});
	            	}
	            }
	            
	            var checkGrounding = function() {
	            	if ($scope.grounding == undefined) $scope.grounding = {};
	            }
	            
	           
	            
	            var removeFormat = function(transportFormats, formatClass) {
	            	angular.forEach(transportFormats, function(format) {
	            		angular.forEach(format.rdfType, function(type) {
	            			if (type == formatClass) transportFormats.splice(transportFormats.indexOf(format), 1);
	            		})
	            	});
	            }
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
    }).directive('domainConceptInput', function(restApi) {
    	return {
    		restrict : 'E',
    		templateUrl : 'modules/editor/directives/domain-concept-input.tmpl.html',
    		scope : {
    			staticProperty : "=",
    			autoCompleteStaticProperty : "="
    		},
    		controller: function($scope, $element)  {
    			 
    			$scope.querySearch = querySearch;
    			$scope.selectedItemChange = selectedItemChange;
    			$scope.searchTextChange   = searchTextChange;
    			     		
    			$scope.availableDomainProperties = {};
    				
				$scope.loadDomainConcepts = function(item) {
					var query = {};
					query.requiredClass = item.input.properties.requiredClass;
					query.requiredProperties = [];
					angular.forEach(item.input.properties.supportedProperties, function(p) {
						var propertyObj = {};
						propertyObj.propertyId = p.propertyId;
						query.requiredProperties.push(propertyObj);
					});
					
					restApi.getDomainKnowledgeItems(query)
						.success(function(queryResponse){
			                if (!$scope.availableDomainProperties[item.elementId])
			            	{
			            		$scope.availableDomainProperties[item.elementId] = {};
			            	}
			                angular.forEach(queryResponse.requiredProperties, function(resp) {
			                    	angular.forEach(resp.queryResponse, function(instanceResult) {
			                    	if (!$scope.availableDomainProperties[item.elementId][resp.propertyId])
			                    		$scope.availableDomainProperties[item.elementId][resp.propertyId] = [];
			                    	var instanceData = {label : instanceResult.label, description : instanceResult.description, propertyValue : instanceResult.propertyValue};
			                    	$scope.availableDomainProperties[item.elementId][resp.propertyId].push(instanceData);
			                	});
			                });
			            })
			            .error(function(msg){
			                console.log(msg);
			            });
				}
    				
    			function querySearch (query, staticPropertyId) {
    			    	var result = [];
    			    	var i = 0;
    			    	angular.forEach($scope.availableDomainProperties[staticPropertyId], function(values) {
    			    		if (values.length > 0 && i == 0)
    						{
    			    			var position = 0;
    			    			angular.forEach(values, function(value) {
    			    				if (query == undefined || value.label.substring(0, query.length) === query) result.push({label : value.label, description: value.description, position : position});
    			    				position++;
    			    			})
    			        		i++;
    						}		
    			    	});
    			    	return result;
    			}
    			    
			    function searchTextChange(text) {
			    
			    }
    			    
			    function selectedItemChange(item, staticPropertyId, supportedProperties) {
			    	angular.forEach(supportedProperties, function(supportedProperty) {
			    	    supportedProperty.value = $scope.availableDomainProperties[staticPropertyId][supportedProperty.propertyId][item.position].propertyValue;
			    	});
    			}
			    
			    $scope.loadDomainConcepts($scope.autoCompleteStaticProperty);	
 
    		 }
    	}
    });