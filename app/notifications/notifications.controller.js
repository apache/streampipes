NotificationsCtrl.$inject = ['$scope', 'restApi'];

export default function NotificationsCtrl($scope, restApi) {
	$scope.notifications = [{}];
	$scope.unreadNotifications = [];
		
	$scope.getNotifications = function(){
        $scope.unreadNotifications = [];
        restApi.getNotifications()
            .success(function(notifications){
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
            })
            .error(function(msg){
                console.log(msg);
            });
    };
    
    
    $scope.getNotifications();
};
