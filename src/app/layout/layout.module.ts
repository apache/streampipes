import angular from 'angular';
import ngCookies from 'angular-cookies';
import 'npm/angular-notification-icons';

import spServices from '../services/services.module'
import {AppCtrl} from './app.controller'

export default angular.module('sp.layout', [spServices, ngCookies, 'angular-notification-icons', 'ngAnimate'])
	.controller('AppCtrl', AppCtrl)
	.name;
