import angular from 'npm/angular';

import ProasenseHomeCtrl from './proasense-home.controller';

export default angular.module('sp.proasenseHome', [])
	.controller('HomeCtrl', ProasenseHomeCtrl)
.name;
