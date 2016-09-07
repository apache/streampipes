import angular from 'npm/angular';

import spConstants from '../constants/constants.module'

import imageChecker from './image-checker.service'
import restApi from './rest-api.service'
import auth from './auth.service'
import domainProperties from './domain-properties.service'
import httpInterceptor from './http-interceptor.service'
import measurementUnits from './measurement-units.service'
import deploymentService from './deployment.service'

export default angular.module('sp.services', [spConstants])
	.factory('imageChecker', imageChecker)
	.service('authService', auth)
	.factory('restApi', restApi)
	.factory('domainPropertiesService', domainProperties)
	.factory('httpInterceptor', httpInterceptor)
	.factory('measurementUnits', measurementUnits)
	.factory('deploymentService', deploymentService)
	.name;
