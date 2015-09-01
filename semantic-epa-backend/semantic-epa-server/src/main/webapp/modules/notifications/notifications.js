var spRecommendations = angular.module('spRecommendations', ['ngMaterial','ngMdIcons'])
.controller('RecommendationCtrl', function($rootScope, $scope, $timeout, $log, $location, $http, restApi, $mdToast) {
	
	$scope.notifications = [{"title" : "ABC", "targetedActor" : "riemer", "description" : "testtet"}];
	$scope.unreadNotifications = [];
	
	$scope.getNotifications = function(){
        restApi.getNotifications()
            .success(function(notifications){
                $scope.notifications = notifications
                console.log($scope.notifications);
            })
            .error(function(msg){
                console.log(msg);
            });
    };
        
    $scope.changeNotificationStatus = function(notificationId, index){
        restApi.updateNotification(no)
            .success(function(success){
            	$scope.notifications[index].read = !$scope.notifications[index].read;
                console.log(success);
            })
            .error(function(msg){
                console.log(msg);
            });
    };
    
    
	
});