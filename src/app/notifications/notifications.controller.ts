import * as angular from 'angular';

export class NotificationsCtrl {

    RestApi: any;
    $rootScope: any;
    notifications: any;
    unreadNotifications: any;

    constructor(RestApi, $rootScope) {
        this.RestApi = RestApi;
        this.$rootScope = $rootScope;
        this.notifications = [{}];
        this.unreadNotifications = [];
    }

    $onInit() {
        this.getNotifications();
    }

    getNotifications() {
        this.unreadNotifications = [];
        this.RestApi.getNotifications()
            .then(notifications => {
                this.notifications = notifications.data;
                this.getUnreadNotifications();
            });
    };

    getUnreadNotifications() {
        angular.forEach(this.notifications, (value) => {
            if (!value.read) {
                this.unreadNotifications.push(value);
            }
        });
    }

    changeNotificationStatus(notificationId) {
        this.RestApi.updateNotification(notificationId)
            .then(success => {
                this.getNotifications();
                this.$rootScope.updateUnreadNotifications();
            });
    };
};

NotificationsCtrl.$inject = ['RestApi', '$rootScope'];
