import angular from 'angular';
import 'npm/dagre';

import spConstants from '../constants/constants.module'

import { ImageChecker } from './image-checker.service'
import { AuthService } from './auth.service'
import { ElementIconText } from './get-element-icon-text.service'
import { InitTooltips } from './init-tooltips.service'
import { RestApi } from './rest-api.service'
import { AuthStatusService } from './auth-status.service'

import domainProperties from './domain-properties.service'
import httpInterceptor from './http-interceptor.service'
import measurementUnits from './measurement-units.service'
import deploymentService from './deployment.service'
import objectProvider from './object-provider.service'
import pipelinePositioningService from './pipeline-positioning.service'
import jsplumbService from './jsplumb.service'
import jsplumbConfigService from './jsplumb-config.service'
import pipelineElementIconService from './pipeline-icon.service'
import pipelineElementOptions from '../editor/components/pipeline-element-options/pipeline-element-options.directive'
import pipelineEditorService from './pipeline-editor.service'

export default angular.module('sp.services', [spConstants])
	.service('ImageChecker', ImageChecker)
	.service('AuthService', AuthService)
	.service('ElementIconText', ElementIconText)
	.service('InitTooltips', InitTooltips)
	.service('RestApi', RestApi)
	.service('AuthStatusService', AuthStatusService)

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
