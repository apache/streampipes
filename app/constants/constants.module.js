import angular from 'npm/angular';

import apiConstants from './general.constants.js'

export default angular.module('sp.constans', [])
	.constant("apiConstants", apiConstants)
	.name;

