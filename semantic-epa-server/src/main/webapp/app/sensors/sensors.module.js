import angular from 'npm/angular';

import SensorsCtrl from './sensors.controller';
import startsWithLetter from './starts-with-letter.filter';

import generatedElementDescription from './directives/deployment/generated-element-description.directive'
import deploymentType from './directives/deployment/deployment-type.directive'
import deployment from './directives/deployment/deployment.directive'
import generatedElementImplementation from './directives/deployment/generated-element-implementation.directive'

export default angular.module('sp.sensors', [])
	.controller('SensorsCtrl', SensorsCtrl)
	.filter('startsWithLetter', startsWithLetter)
	.directive('generatedElementDescription', generatedElementDescription)
	.directive('deploymentType', deploymentType)
	.directive('deployment', deployment)
	.directive('generatedElementImplementation', generatedElementDescription)
.name;
