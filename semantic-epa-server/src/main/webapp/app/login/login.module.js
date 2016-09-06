import angular from 'npm/angular';

import RegisterCtrl from './register.controller'

export default angular.module('sp.login', [])
	.controller('RegisterCtrl', RegisterCtrl)
	.name;
