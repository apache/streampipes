import * as angular from 'angular';
import 'npm/dagre';

import spConstants from '../constants/constants.module'

import { ImageChecker } from './image-checker.service'
import { AuthService } from './auth.service'
import { ElementIconText } from './get-element-icon-text.service'
import { InitTooltips } from './init-tooltips.service'
import { RestApi } from './rest-api.service'
import { AuthStatusService } from './auth-status.service'
import { DomainProperties } from './domain-properties.service'
import { JsplumbBridge } from './jsplumb-bridge.service'
import { JsplumbService } from './jsplumb.service'
import { PipelinePositioningService } from './pipeline-positioning.service'
import { PipelineEditorService } from './pipeline-editor.service'
import { DialogBuilder } from './dialog-builder.service'
import { MeasurementUnits } from './measurement-units.service'
import { DeploymentService } from './deployment.service'
import { JsplumbConfigService } from './jsplumb-config.service'
import { PipelineElementIconService } from './pipeline-icon.service'
import { ObjectProvider } from './object-provider.service'

//mport { PipelineElementOptionsComponent } from '../editor/components/pipeline-element-options/pipeline-element-options.component'

export default angular.module('sp.services', [spConstants])
	.service('ImageChecker', ImageChecker)
	.service('AuthService', AuthService)
	.service('ElementIconText', ElementIconText)
	.service('InitTooltips', InitTooltips)
	.service('RestApi', RestApi)
	.service('AuthStatusService', AuthStatusService)
	.service('ObjectProvider', ObjectProvider)
	.service('DomainProperties', DomainProperties)
	.service('JsplumbBridge', JsplumbBridge)
	.service('JsplumbService', JsplumbService)
	.service('PipelinePositioningService', PipelinePositioningService)
	.service('PipelineEditorService', PipelineEditorService)
	.service('DialogBuilder', DialogBuilder)
    .service('MeasurementUnitsService', MeasurementUnits)
    .service('DeploymentService', DeploymentService)
    .service('JsplumbConfigService', JsplumbConfigService)
    .service('PipelineElementIconService', PipelineElementIconService)
	//.component('pipelineElementOptions', PipelineElementOptionsComponent)
	.name;
