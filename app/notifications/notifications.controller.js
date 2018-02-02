export class NotificationsCtrl {

    constructor(restApi, $rootScope) {
        this.restApi = restApi;
        this.$rootScope = $rootScope;
        this.notifications = [{}];
        this.unreadNotifications = [];
        this.getNotifications();
    }

    getNotifications() {
        this.unreadNotifications = [];
        this.restApi.getNotifications()
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
        this.restApi.updateNotification(notificationId)
            .success(success => {
                this.getNotifications();
                this.$rootScope.updateUnreadNotifications();
            })
            .error(msg => {
                console.log(msg);
            });
    };
};

NotificationsCtrl.$inject = ['restApi', '$rootScope'];
