import angular from 'npm/angular';

import SensorsCtrl from './sensors.controller';
import startsWithLetter from './starts-with-letter.filter';

export default angular.module('sp.sensors', [])
	.controller('SensorsCtrl', SensorsCtrl)
	.filter('startsWithLetter', startsWithLetter)
.name;
