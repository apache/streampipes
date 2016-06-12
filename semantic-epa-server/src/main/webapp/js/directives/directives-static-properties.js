angular
    .module('streamPipesApp')
    .directive('staticProperties', function(restApi) {
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
    })
   .directive('domainConceptProperty', function(restApi) {
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