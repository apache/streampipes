import angular from 'npm/angular';
import 'npm/dagre';

import spConstants from '../constants/constants.module'

import imageChecker from './image-checker.service'
import restApi from './rest-api.service'
import auth from './auth.service'
import domainProperties from './domain-properties.service'
import getElementIconText from './get-element-icon-text.service'
import initTooltips from './init-tooltips.service'
import httpInterceptor from './http-interceptor.service'
import measurementUnits from './measurement-units.service'
import deploymentService from './deployment.service'
import objectProvider from './object-provider.service'
import pipelinePositioningService from './pipeline-positioning.service'
import jsplumbService from './jsplumb.service'
import jsplumbConfigService from './jsplumb-config.service'
import pipelineElementIconService from './pipeline-icon.service'
import pipelineElementOptions from '../editor/directives/pipeline-element-options/pipeline-element-options.directive'
import pipelineEditorService from './pipeline-editor.service'

export default angular.module('sp.services', [spConstants])
	.factory('imageChecker', imageChecker)
	.service('authService', auth)
	.service('getElementIconText', getElementIconText)
	.service('initTooltips', initTooltips)
	.factory('restApi', restApi)
	.service('objectProvider', objectProvider)
	.factory('domainPropertiesService', domainProperties)
	.factory('httpInterceptor', httpInterceptor)
	.factory('measurementUnitsService', measurementUnits)
	.factory('deploymentService', deploymentService)
	.factory('jsplumbService', jsplumbService)
	.factory('jsplumbConfigService', jsplumbConfigService)
	.factory('pipelineElementIconService', pipelineElementIconService)
	.factory('pipelinePositioningService', pipelinePositioningService)
	.factory('pipelineEditorService', pipelineEditorService)
	.directive('pipelineElementOptions', pipelineElementOptions)
	.name;
