import angular from 'npm/angular';
import ngCookies from 'npm/angular-cookies';
import 'npm/angular-notification-icons';

import spServices from '../services/services.module'
import {AppCtrl} from './app.controller'

export default angular.module('sp.layout', [spServices, ngCookies, 'angular-notification-icons', 'ngAnimate'])
	.controller('AppCtrl', AppCtrl)
	.name;
