export class NotificationsCtrl {

    constructor(RestApi, $rootScope) {
        this.RestApi = RestApi;
        this.$rootScope = $rootScope;
        this.notifications = [{}];
        this.unreadNotifications = [];
        this.getNotifications();
    }

    getNotifications() {
        this.unreadNotifications = [];
        this.RestApi.getNotifications()
            .success(notifications => {
                this.notifications = notifications;
                this.getUnreadNotifications();
            })
            .error(msg => {
                console.log(msg);
            });
    };

    getUnreadNotifications() {
        angular.forEach(this.notifications, function (value) {
            if (!value.read) this.unreadNotifications.push(value);
        });
    }

    changeNotificationStatus(notificationId) {
        this.RestApi.updateNotification(notificationId)
            .success(success => {
                this.getNotifications();
                this.$rootScope.updateUnreadNotifications();
            })
            .error(msg => {
                console.log(msg);
            });
    };
};

NotificationsCtrl.$inject = ['RestApi', '$rootScope'];
