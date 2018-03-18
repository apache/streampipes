import * as angular from 'angular';

import {NotificationsCtrl} from './notifications.controller';

export default angular.module('sp.notifications', [])
	.controller('NotificationsCtrl', NotificationsCtrl)
	.name;

