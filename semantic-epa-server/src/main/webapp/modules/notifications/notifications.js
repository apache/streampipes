angular.module('streamPipesApp')
.controller('RecommendationCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, restApi, $mdToast) {
	
	$scope.notifications = [{}];
	$scope.unreadNotifications = [];
		
	$scope.getNotifications = function(){
        restApi.getNotifications()
            .success(function(notifications){
                $scope.notifications = notifications;
                $scope.getUnreadNotifications();
                console.log($scope.notifications);
            })
            .error(function(msg){
                console.log(msg);
            });
    };
    
    $scope.getUnreadNotifications = function() {
    	angular.forEach($scope.notifications, function(value, key) {
    		console.log(value);
    		if (!value.read) $scope.unreadNotifications.push(value);
    	});
    }
        
    $scope.changeNotificationStatus = function(notificationId){
        restApi.updateNotification(notificationId)
            .success(function(success){
            	$scope.getNotifications();
                console.log(success);
            })
            .error(function(msg){
                console.log(msg);
            });
    };
    
    
    $scope.getNotifications();
    
	
});