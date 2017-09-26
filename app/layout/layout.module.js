import angular from 'npm/angular';
import ngCookies from 'npm/angular-cookies';

import spServices from '../services/services.module'
import AppCtrl from './app.controller'
import LeftCtrl from './left.controller'
import SettingsCtrl from './settings.controller'

export default angular.module('sp.layout', [spServices, ngCookies])
	.controller('AppCtrl', AppCtrl)
	.controller('LeftCtrl', LeftCtrl)
	.controller('SettingsCtrl', SettingsCtrl)
	.directive('iframeAutoSize', SettingsCtrl)
	.directive('ngRightClick', SettingsCtrl)
	.directive('userAvatar', SettingsCtrl)
	.name;
