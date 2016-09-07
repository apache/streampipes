import angular from 'npm/angular';
import AddCtrl from './add.controller'
import marketplaceDirective from './marketplace.directive'

export default angular.module('sp.add', [])
	.controller('AddCtrl', AddCtrl)
	.directive('marketplace', marketplaceDirective)
	.name;
