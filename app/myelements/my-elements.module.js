import angular from 'npm/angular';

import MyElementsCtrl from './my-elements.controller'

export default angular.module('sp.myElements', [])
	.controller('MyElementsCtrl', MyElementsCtrl)
	.name;
