NotificationsCtrl.$inject = ['$rootScope', '$scope', 'restApi'];

export default function NotificationsCtrl($rootScope, $scope, restApi) {
	$scope.notifications = [{}];
	$scope.unreadNotifications = [];
		
	$scope.getNotifications = function(){
        $scope.unreadNotifications = [];
        restApi.getNotifications()
            .success(function(notifications){
                console.log(notifications);
                $scope.notifications = notifications;
                $scope.getUnreadNotifications();
            })
            .error(function(msg){
                console.log(msg);
            });
    };
    
    $scope.getUnreadNotifications = function() {
    	angular.forEach($scope.notifications, function(value, key) {
    		if (!value.read) $scope.unreadNotifications.push(value);
    	});
    }
        
    $scope.changeNotificationStatus = function(notificationId){
        restApi.updateNotification(notificationId)
            .success(function(success){
            	$scope.getNotifications();
                $rootScope.updateUnreadNotifications();
            })
            .error(function(msg){
                console.log(msg);
            });
    };
    
    
    $scope.getNotifications();
};
