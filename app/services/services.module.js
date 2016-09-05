import angular from 'npm/angular';

import spConstants from '../constants/constants.module'

import restApi from './rest-api.service'
import authService from './auth.service'

export default angular.module('sp.services', [spConstants])
	.service('authService', authService)
	.factory('restApi', restApi)
	.name;

