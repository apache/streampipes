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
    })