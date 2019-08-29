/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
