import angular from 'angular';

import {HomeCtrl} from './home.controller'
import {HomeService} from "./home.service";

export default angular.module('sp.home', [])
	.controller('HomeCtrl', HomeCtrl)
	.service('HomeService', HomeService)
	.name;
