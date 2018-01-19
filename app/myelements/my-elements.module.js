import angular from 'angular';

import {MyElementsCtrl} from './my-elements.controller'

export default angular.module('sp.myElements', [])
	.controller('MyElementsCtrl', MyElementsCtrl)
	.name;
