import angular from 'npm/angular';

import NotificationsCtrl from './notifications.controller';

export default angular.module('sp.notifications', [])
	.controller('NotificationsCtrl', NotificationsCtrl)
	.name;

