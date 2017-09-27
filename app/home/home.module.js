import angular from 'npm/angular';

import HomeCtrl from './home.controller'

export default angular.module('sp.home', [])
	.controller('HomeCtrl', HomeCtrl)
	.name;
