var spCreate = angular.module('spCreate', ['ngMaterial','ngMdIcons'])
.controller('CreateCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, restApi, $mdToast) {
	
	$scope.schema = {};
	
	
	
	$scope.loadSchema = function() {
		$http.get("http://localhost:8080/semantic-epa-backend/api/v2/users/" +$rootScope.email +"/sources/jsonschema")
        .then(
          function(response) {
        	  $scope.schema = response.data;
        	  console.log($scope.schema);
          })
	};
	
	$scope.form = [
	               "name",
	               "description",
	               "iconUrl",
	               {
	            	   "title" : "Event Streams",
	            	   "type" : "array",
	            	   "add" : "Add Event Stream",
	            	   "key": "eventStreams",
	            	   "items": [
	            	             "eventStreams[].name",
	            	             "eventStreams[].description",
	            	             "eventStreams[].iconUrl",
	            	            
	            	             {
	            	            	 "title" : "Event Grounding",
	            	            	 "type": "array",
	            	            	 "key" : "eventStreams[].eventGrounding",
	            	            	 "add": "Add Event Grounding",
	            	                 "remove": "Remove Event Grounding",
	            	            	 "items" : [
	            	            	           
	            	            	            	         "eventStreams[].eventGrounding[].elementName"
	            	            	            	         
	            	            	           ]
	            	            
	            	             
	    		               },
	    		               {
	            	            	 "title" : "Event Properties",
	            	            	 "type": "array",
	            	            	 "key" : "eventStreams[].eventSchema.eventProperties",
	            	            	 "add": "Add Event Properties",
	            	                 "remove": "Remove Event Property",
	            	            	 "items" : [
	            	            	           
	            	            	            	         "eventStreams[].eventSchema.eventProperties[].propertyType"

	            	            	           ]
	            	                         
	    		               }
	            	            ]
	               },
	               
];

	$scope.model = {};
	$scope.loadSchema();
	
});