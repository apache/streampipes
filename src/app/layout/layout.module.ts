import * as angular from 'angular';
import ngCookies from 'angular-cookies';
import 'npm/angular-notification-icons';

import spServices from '../services/services.module'
import {AppCtrl} from './app.controller'
import {ToolbarController} from "./toolbar.controller";
import {FeedbackComponent} from "./components/feedback.component";

export default angular.module('sp.layout', [spServices, ngCookies, 'angular-notification-icons', 'ngAnimate'])
	.component('spFeedback', FeedbackComponent)
	.controller('AppCtrl', AppCtrl)
	.controller('ToolbarController', ToolbarController)
	.name;
