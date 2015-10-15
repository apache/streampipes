angular.module('streamPipesApp')
.controller('OntologyCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, restApi, $mdToast, $animate, $mdDialog) {
	
	console.log("OntologyCtrl");
	
	$scope.primitiveClasses = [{"title" : "String", "description" : "A textual datatype, e.g., 'machine1'", "id" : "http://www.w3.org/2001/XMLSchema#string"},
	                           {"title" : "Boolean", "description" : "A true/false value", "id" : "http://www.w3.org/2001/XMLSchema#boolean"},
	                           {"title" : "Integer", "description" : "A whole-numerical datatype, e.g., '1'", "id" : "http://www.w3.org/2001/XMLSchema#integer"},
	                           {"title" : "Double", "description" : "A floating-point number, e.g., '1.25'", "id" : "http://www.w3.org/2001/XMLSchema#float"}];
	
	$scope.rangeTypes = [{"title" : "Primitive Type", "description" : "A primitive type, e.g., a number or a textual value", "rangeType" : "PRIMITIVE"},
	                           {"title" : "Enumeration", "description" : "A textual value with a specified value set", "rangeType" : "ENUMERATION"},
	                           {"title" : "Quantitative Value", "description" : "A numerical value within a specified range", "rangeType" : "QUANTITATIVE_VALUE"}];
	
	$scope.properties = [];
	$scope.propertySelected = false;
	$scope.propertyDetail = {};
	$scope.selectedPrimitive = $scope.primitiveClasses[0];
	$scope.selectedRangeType = $scope.rangeTypes[0].rangeType;
	
	$scope.concepts = [];
	$scope.conceptSelected = false;
	$scope.conceptDetail = {};
	$scope.selectedClassProperty = "";
	
	$scope.instanceSelected = false;
	$scope.instanceDetail = {};
	
	
	$scope.loadProperties = function(){
        restApi.getOntologyProperties()
            .success(function(propertiesData){
                $scope.properties = propertiesData;
                console.log($scope.properties);
            })
            .error(function(msg){
                console.log(msg);
            });
    };
    
    $scope.loadConcepts = function(){
        restApi.getOntologyConcepts()
            .success(function(conceptsData){
                $scope.concepts = conceptsData;
                console.log($scope.concepts);
            })
            .error(function(msg){
                console.log(msg);
            });
    };
    
    $scope.loadPropertyDetails = function(propertyId){
        restApi.getOntologyPropertyDetails(propertyId)
            .success(function(propertiesData){
                $scope.propertyDetail = propertiesData;
                $scope.propertySelected = true;
                console.log($scope.propertyDetail);
            })
            .error(function(msg){
            	$scope.propertySelected = false;
                console.log(msg);
            });
    };
    
    $scope.loadConceptDetails = function(conceptId){
    	$scope.instanceSelected = false;
        restApi.getOntologyConceptDetails(conceptId)
            .success(function(conceptData){
                $scope.conceptDetail = conceptData;
                $scope.conceptSelected = true;
                console.log($scope.conceptDetail);
            })
            .error(function(msg){
            	$scope.conceptSelected = false;
                console.log(msg);
            });
    };
    
    $scope.loadInstanceDetails = function(instanceId){
        restApi.getOntologyInstanceDetails(instanceId)
            .success(function(instanceData){
                $scope.instanceDetail = instanceData;
                $scope.instanceSelected = true;
                console.log($scope.instanceDetail);
            })
            .error(function(msg){
            	$scope.instanceSelected = false;
                console.log(msg);
            });
    };
    
    $scope.addPropertyToClass = function() {
    	if (!$scope.conceptDetail.domainProperties) $scope.conceptDetail.domainProperties = [];
    	 restApi.getOntologyPropertyDetails($scope.selectedClassProperty)
         .success(function(propertiesData){
        	 $scope.conceptDetail.domainProperties.push(propertiesData);
         })
         .error(function(msg){
             console.log(msg);
         });
    }
    
    $scope.removePropertyFromClass = function(property) {
    	$scope.conceptDetail.domainProperties.splice($scope.conceptDetail.domainProperties.indexOf(property), 1);
    }
    
    $scope.storeClass = function() {
    	$scope.loading = true;
    	console.log($scope.conceptDetail);
    	restApi.updateOntologyConcept($scope.conceptDetail.elementHeader.id, $scope.conceptDetail)
	    	.success(function(msg){
	    		$scope.loading = false;
	        })
	        .error(function(msg){
	        	$scope.loading = false;
	
	        });
    }
    
    $scope.addTypeDefinition = function() {
    	$scope.propertyDetail.range = {};
    	$scope.propertyDetail.range.rangeType = $scope.selectedRangeType;
    	$scope.propertyDetail.rangeDefined = true;
    	console.log($scope.selectedRangeType);
    	if ($scope.selectedRangeType === 'PRIMITIVE')
		{
    		$scope.propertyDetail.rdfsDatatype = "";
		} else if ($scope.selectedRangeType === 'QUANTITATIVE_VALUE')
		{
			$scope.propertyDetail.range.minValue = -1;
			$scope.propertyDetail.range.maxValue = -1;
			$scope.propertyDetail.range.unitCode = "";
	    } else if ($scope.selectedRangeType === 'ENUMERATION')
		{
			$scope.propertyDetail.range.minValue = -1;
			$scope.propertyDetail.range.maxValue = -1;
			$scope.propertyDetail.range.unitCode = "";
		}
    }
    
    $scope.reset = function() {
    	$scope.propertyDetail.rangeDefined = false;
    }
	
    $scope.updateProperty = function() {
    	$scope.loading = true;
    	$scope.propertyDetail.labelDefined = true;
    	console.log($scope.propertyDetail);
    	restApi.updateOntologyProperty($scope.propertyDetail.elementHeader.id, $scope.propertyDetail)
	    	.success(function(msg){
	    		$scope.loading = false;
	        })
	        .error(function(msg){
	        	$scope.loading = false;
	
	        });
    }
    
    $scope.openNamespaceDialog = function(){
   	 $mdDialog.show({
   	      controller: DialogController,
   	      templateUrl: 'modules/ontology/templates/manageNamespacesDialog.tmpl.html',
   	      parent: angular.element(document.body),
   	      clickOutsideToClose:true,
   	    })
   };
   
   $scope.openAddElementDialog = function(elementType){
   		$scope.openAddElementDialog(elementType, undefined);
	};
	
	 $scope.openAddElementDialog = function(elementType, conceptId){
		 $mdDialog.show({
	   	      controller: AddDialogController,
	   	      templateUrl: 'modules/ontology/templates/createElementDialog.tmpl.html',
	   	      parent: angular.element(document.body),
	   	      clickOutsideToClose:true,
	   	      scope:$scope,
	   	      preserveScope:true,
		   	  locals : {
		   		  elementType : elementType,
		   		  conceptId : conceptId
		      }
	   	    })
	 }
    
	$scope.$on('loadProperty', function(event, propertyId) {
		console.log("hi");
		$scope.loadPropertyDetails(propertyId);
	});
        
    $scope.loadProperties();
    $scope.loadConcepts();
    
   
    
    
});

function AddDialogController($scope, $mdDialog, restApi, elementType, conceptId) {
    
	$scope.elementData = {};
	$scope.elementData.namespace = "";
	$scope.elementData.elementName = "";
	$scope.elementData.instanceOf = conceptId;
	$scope.elementType = elementType;
	$scope.conceptId = conceptId;
	$scope.namespaces = [];
	
	$scope.getNamespaces = function() {
   	 restApi.getOntologyNamespaces()
        .success(function(namespaces){
            $scope.namespaces = namespaces;
            console.log(namespaces);
        })
        .error(function(msg){
            console.log(msg);
        });
   }
	    	
	$scope.add = function() {
		var promise;
		if (elementType === 'Property') 
		{
			restApi.addOntologyProperty($scope.elementData)
    			.success(function(msg){
    				$scope.loadProperties();
    				$scope.loadPropertyDetails($scope.elementData.namespace +":" +$scope.elementData.elementName);
    			});
    	}
		else if (elementType === 'Concept') 
		{
			restApi.addOntologyConcept($scope.elementData)
			.success(function(msg){
				$scope.loadConcepts();
				$scope.loadConceptDetails($scope.elementData.namespace +":" +$scope.elementData.elementName);
			});
		}
		else 
		{
			restApi.addOntologyInstance($scope.elementData).success(function(msg){
				console.log($scope.elementData);
				
				$scope.loadConcepts();
				$scope.loadConceptDetails(conceptId);
				$scope.loadInstanceDetails($scope.elementData.namespace +":" +$scope.elementData.elementName);
			});
		}
		
		console.log($scope.elementData.namespace);
		$scope.hide();
	};
	
	$scope.hide = function() {
  		$mdDialog.hide();
  	};
  	
  	$scope.cancel = function() {
  	    $mdDialog.cancel();
  	};
  	
  	$scope.getNamespaces();
}

function DialogController($scope, $mdDialog, restApi) {
	
	$scope.namespaces = [];
	$scope.addSelected = false;
	$scope.newNamespace = {};
	
	$scope.getNamespaces = function() {
    	 restApi.getOntologyNamespaces()
         .success(function(namespaces){
             $scope.namespaces = namespaces;
             console.log(namespaces);
         })
         .error(function(msg){
             console.log(msg);
         });
    }
	
	$scope.addNamespace = function() {
		restApi.addOntologyNamespace($scope.newNamespace)
		 .success(function(msg){
			 $scope.addSelected = false;
			 $scope.newNamespace = {};
			 $scope.getNamespaces();
         })
         .error(function(msg){
        	 $scope.addSelected = false;
             console.log(msg);
         }); 		
	}
	
	$scope.deleteNamespace = function(prefix) {
		restApi.deleteOntologyNamespace(prefix)
   		 .success(function(msg){
   			 $scope.getNamespaces();
            })
        .error(function(msg){
            console.log(msg);
        }); 	
	}
	
	$scope.showAddInput = function() {
		$scope.addSelected = true;
		$scope.newNamespace.prefix = "";
		$scope.newNamespace.name = "";
	}
	
  	$scope.hide = function() {
  		$mdDialog.hide();
  	};
  	
  	$scope.cancel = function() {
  	    $mdDialog.cancel();
  	};
  	
  	$scope.getNamespaces();
	}
	