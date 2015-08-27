var spAdd = angular.module('spAdd', ['ngMaterial','ngMdIcons'])
.controller('AddCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, restApi) {
	
	$scope.elements;
	$scope.endpointUrl;
	$scope.endpointData;
	$scope.results = [];
	$scope.loading = false;
	$scope.marketplace = false;
	
	$scope.addFromEndpoint = function () {
		$scope.loading = true;		
		restApi.addBatch($scope.endpointUrl, true)
        .success(function (data) {
        	$scope.loading = false;
        	angular.forEach(data, function(element, index) {
        		$scope.results[index] = {};
        		$scope.results[index].success = element.success;
        		$scope.results[index].msg = element.notifications[0].description;
        	});
        })
	}
	
	$scope.add = function () {
		$scope.loading = true;
        var uris = $scope.elements.split(" ");
        $scope.addElements(uris, 0);
	}
	
	$scope.addElements = function (uris, i) {
	    if (i == uris.length) {
	    	$scope.loading = false;
	        return;
	    } else {
	    	var uri = uris[i];		
	    	$scope.results[i] = {};
	    	$scope.results[i].elementId = uri;
	    	$scope.results[i].loading = true;        
	        uri = encodeURIComponent(uri);
	        
	        restApi.add(uri, true)
            .success(function (data) {
            	 $scope.results[i].loading = false;
            	 $scope.results[i].msg = data.notifications[0].description;
            })
            .error(function (data) {
            	 $scope.results[i].loading = false;
            	 $scope.results[i].msg = data.notifications[0].description;
            })
            .then(function () {
	            $scope.addElements(uris, ++i);
	        });
	    }
	}		   
});