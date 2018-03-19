import * as angular from 'angular';

import apiConstants from './general.constants'

export default angular.module('sp.constants', [])
	.constant("apiConstants", apiConstants)
	.name;