/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import * as angular from 'angular';
import 'npm/dagre';

import spConstants from '../constants/constants.module'

import {ImageChecker} from './image-checker.service'
import {AuthService} from './auth.service'
import {ElementIconText} from './get-element-icon-text.service'
import {InitTooltips} from './init-tooltips.service'
import {RestApi} from './rest-api.service'
import {AuthStatusService} from './auth-status.service'
import {DomainProperties} from './domain-properties.service'
import {JsplumbBridge} from '../editor/services/jsplumb-bridge.service'
import {JsplumbService} from '../editor/services/jsplumb.service'
import {PipelinePositioningService} from '../editor/services/pipeline-positioning.service'
import {PipelineEditorService} from '../editor/services/pipeline-editor.service'
import {DialogBuilder} from './dialog-builder.service'
import {MeasurementUnits} from './measurement-units.service'
import {DeploymentService} from './deployment.service'
import {JsplumbConfigService} from '../editor/services/jsplumb-config.service'
import {PipelineElementIconService} from './pipeline-icon.service'
import {ObjectProvider} from '../editor/services/object-provider.service'
import {downgradeInjectable} from '@angular/upgrade/static';
import {ShepherdService} from "./tour/shepherd.service";
import {TourProviderService} from "./tour/tour-provider.service";
import {PropertySelectorService} from "./property-selector.service";
import {RouteTransitionInterceptorService} from "./route-transition-interceptor.service";

import CreatePipelineTour from './tour/create-pipeline-tour.constants';
import DashboardTour from './tour/dashboard-tour.constants';
import AdapterTour from './tour/adapter-tour.constants';
import AdapterTour2 from './tour/adapter-tour-2.constants';
import AdapterTour3 from './tour/adapter-tour-3.constants';
import {NotificationCountService} from "./notification-count-service";


export default angular.module('sp.services', [spConstants])
	.service('ImageChecker', ImageChecker)
	.service('AuthService', AuthService)
	.service('ElementIconText', ElementIconText)
	.service('InitTooltips', InitTooltips)
	.service('RestApi', RestApi)
	.service('AuthStatusService', downgradeInjectable(AuthStatusService))
	.service('ObjectProvider', ObjectProvider)
	.service('DomainProperties', DomainProperties)
	//.service('JsplumbBridge', downgradeInjectable(JsplumbBridge))
	//.service('JsplumbService', downgradeInjectable(JsplumbService))
	//.service('PipelinePositioningService', downgradeInjectable(PipelinePositioningService))
	//.service('PipelineEditorService', PipelineEditorService)
	.service('DialogBuilder', DialogBuilder)
    .service('MeasurementUnitsService', MeasurementUnits)
    .service('DeploymentService', DeploymentService)
    .service('JsplumbConfigService', JsplumbConfigService)
    .service('PipelineElementIconService', PipelineElementIconService)
	.service('RouteTransitionInterceptorService', RouteTransitionInterceptorService)
	.service('ShepherdService', ShepherdService)
	.service('TourProviderService', TourProviderService)
	.service('PropertySelectorService', PropertySelectorService)
	.service('NotificationCountService', downgradeInjectable(NotificationCountService))
	.constant('createPipelineTourConstants', CreatePipelineTour)
	.constant('dashboardTourConstants', DashboardTour)
	.constant('adapterTourConstants', AdapterTour)
    .constant('adapterTour2Constants', AdapterTour2)
    .constant('adapterTour3Constants', AdapterTour3)
	.name;
