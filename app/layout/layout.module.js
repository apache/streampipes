import angular from 'npm/angular';
import ngCookies from 'npm/angular-cookies';

import spServices from '../services/services.module'
import TopNavCtrl from './top-nav.controller'
import AppCtrl from './app.controller'
import LeftCtrl from './left.controller'
import SettingsCtrl from './settings.controller'

export default angular.module('sp.layout', [spServices, ngCookies])
	.controller('TopNavCtrl', TopNavCtrl)
	.controller('AppCtrl', AppCtrl)
	.controller('LeftCtrl', LeftCtrl)
	.controller('SettingsCtrl', SettingsCtrl)
	.name;
