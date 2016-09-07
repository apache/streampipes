import angular from 'npm/angular';

import DocsCtrl from './docs.controller';

export default angular.module('sp.docs', [])
	.controller('DocsCtrl', DocsCtrl)
	.name;
