CreateCtrl.$inject = ['$rootScope', '$scope', 'restApi', '$mdToast'];

export default function CreateCtrl($rootScope, $scope, restApi, $mdToast) {
	
	$scope.typeSelected = "SEP";
	$scope.selectedSepIndex = 0;
	$scope.selectedSepaIndex = 0;
	$scope.selectedSecIndex = 0;
	
	$scope.deployment = {};
	$scope.deployment.deploymentType = "standalone";
	$scope.deployment.sepaType = "storm";
	$scope.deployment.groupId = "org.streampipes.test";
	$scope.deployment.artifactId = "test-project";
	$scope.deployment.classNamePrefix = "TestProject";
	$scope.deployment.port = 8093;
	
	$scope.availableOutputStrategies = [{"name" : "Rename"}, {"name" : "Enrich"}, {"name" : "Custom"}, {"name" : "Fixed"}, {"name" : "List"}];
	$scope.availableStaticProperties = [{"name" : "Free Text"}, {"name" : "Any Selection"}, {"name" : "OneOf Selection"}, {"name" : "Mapping Property"}, {"name" : "Matching Property"}];
	
	
	
	$scope.submitDeployment = function() {
		if ($scope.deployment.sepaType == 'storm') $scope.submitStormDeployment();
		else  $mdToast.show(
			      $mdToast.simple()
			        .content('Not yet supported')
			        .hideDelay(3000)
			    );
	};
	
	$scope.submitStormDeployment = function() {
		restApi.deployStorm()
				  success(function(data, status, headers, config) {
			    $scope.openSaveAsDialog($scope.deployment.artifactId +".zip", data, "application/zip");
		  }).
		  error(function(data, status, headers, config) {
		    console.log(data);
		  });
	};
	
	$scope.openSaveAsDialog = function(filename, content, mediaType) {
	    var blob = new Blob([content], {type: mediaType});
	    saveAs(blob, filename);
	}
	
	$scope.streamRestrictions = [];
	
	$scope.schema = {};
	
	$scope.switchSepTab = function(newIndex) {
		$scope.selectedSepIndex = newIndex;
	}
	
	$scope.switchSepaTab = function(newIndex) {
		$scope.selectedSepaIndex = newIndex;
	}
	
	$scope.switchSecTab = function(newIndex) {
		$scope.selectedSecIndex = newIndex;
	}
	
	$scope.addStreamRestriction = function() {
		$scope.streamRestrictions.push({"eventProperties" : []});
	}
	
	 $scope.removeStreamRestriction = function(index) {
		    $scope.streamRestrictions.splice(index, 1);
	 };
	 
	 $scope.addPropertyRestriction = function(index) {
			if ($scope.streamRestrictions[index].eventProperties == undefined) $scope.streamRestrictions[index].eventProperties = [];
			 $scope.streamRestrictions[index].eventProperties.push({"datatype" : "abc", "propertyType" : "proptype"});
		}
		
	 $scope.removePropertyRestriction = function(index, propertyIndex) {
		    $scope.streamRestrictions[index].eventProperties.splice(propertyIndex, 1);
	 };
	
	
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
	            	   "key": "spDataStreams",
	            	   "items": [
	            	             "spDataStreams[].name",
	            	             "spDataStreams[].description",
	            	             "spDataStreams[].iconUrl",
	            	            
	            	             {
	            	            	 "title" : "Event Grounding",
	            	            	 "type": "array",
	            	            	 "key" : "spDataStreams[].eventGrounding",
	            	            	 "add": "Add Event Grounding",
	            	                 "remove": "Remove Event Grounding",
	            	            	 "items" : [
	            	            	           
	            	            	            	         "spDataStreams[].eventGrounding[].elementName"
	            	            	            	         
	            	            	           ]
	            	            
	            	             
	    		               },
	    		               {
	            	            	 "title" : "Event Properties",
	            	            	 "type": "array",
	            	            	 "key" : "spDataStreams[].eventSchema.eventProperties",
	            	            	 "add": "Add Event Properties",
	            	                 "remove": "Remove Event Property",
	            	            	 "items" : [
	            	            	           
	            	            	            	         "spDataStreams[].eventSchema.eventProperties[].propertyType"

	            	            	           ]
	            	                         
	    		               }
	            	            ]
	               },
	               
];

	$scope.model = {};
	$scope.loadSchema();
	
};
