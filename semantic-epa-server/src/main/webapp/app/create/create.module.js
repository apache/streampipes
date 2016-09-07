import angular from 'npm/angular';

import CreateCtrl from './create.controller'

export default angular.module('sp.create', [])
	.controller('CreateCtrl', CreateCtrl)
.name;
