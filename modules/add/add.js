angular.module('streamPipesApp')
.controller('AddCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, restApi) {
	
	$scope.elements;
	$scope.endpointUrl;
	$scope.endpointData;
	$scope.results = [];
	$scope.loading = false;
	$scope.marketplace = false;
	
	$scope.selectedTab = "ENDPOINT";
	
	$scope.setSelectedTab = function(type) {
		$scope.selectedTab = type;
	}
	
	$scope.addFromEndpoint = function () {
		$scope.loading = true;		
		restApi.addBatch($scope.endpointUrl, true)
        .success(function (data) {
        	$scope.loading = false;
        	angular.forEach(data, function(element, index) {
        		$scope.results[index] = {};
        		$scope.results[index].success = element.success;
        		$scope.results[index].elementName = element.elementName;
        		$scope.results[index].details = [];
        		angular.forEach(element.notifications, function(notification, i) {
        			var detail = {};
        			detail.description = notification.description;
        			detail.title = notification.title;
        			$scope.results[index].details.push(detail);
        		})
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
            	 $scope.results[i].success = data.success;
            	 $scope.results[i].elementName = data.elementName;
            	 $scope.results[i].details = [];
            	 angular.forEach(data.notifications, function(notification, j) {
         			var detail = {};
         			detail.description = notification.description;
         			detail.title = notification.title;
         			$scope.results[i].details.push(detail);
         		})
            })
            .error(function (data) {
            	 $scope.results[i].loading = false;
            	 $scope.results[i].description = data.notifications[0].description;
            	 $scope.results[i].title = data.notifications[0].title;
            })
            .then(function () {
	            $scope.addElements(uris, ++i);
	        });
	    }
	}		   
});