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


// export default angular.module('sp.services', [spConstants])
// 	.service('ImageChecker', ImageChecker)
// 	.service('AuthService', downgradeInjectable(AuthService))
// 	.service('ElementIconText', ElementIconText)
// 	.service('InitTooltips', InitTooltips)
// 	.service('RestApi', RestApi)
// 	.service('AuthStatusService', downgradeInjectable(AuthStatusService))
// 	.service('ObjectProvider', ObjectProvider)
// 	.service('DomainProperties', DomainProperties)
// 	//.service('JsplumbBridge', downgradeInjectable(JsplumbBridge))
// 	//.service('JsplumbService', downgradeInjectable(JsplumbService))
// 	//.service('PipelinePositioningService', downgradeInjectable(PipelinePositioningService))
// 	//.service('PipelineEditorService', PipelineEditorService)
// 	.service('DialogBuilder', DialogBuilder)
//     .service('MeasurementUnitsService', MeasurementUnits)
//     .service('DeploymentService', DeploymentService)
//     .service('JsplumbConfigService', JsplumbConfigService)
//     .service('PipelineElementIconService', PipelineElementIconService)
// 	.service('RouteTransitionInterceptorService', RouteTransitionInterceptorService)
// 	.service('ShepherdService', ShepherdService)
// 	.service('TourProviderService', TourProviderService)
// 	.service('PropertySelectorService', PropertySelectorService)
// 	.service('NotificationCountService', downgradeInjectable(NotificationCountService))
// 	.constant('createPipelineTourConstants', CreatePipelineTour)
// 	.constant('dashboardTourConstants', DashboardTour)
// 	.constant('adapterTourConstants', AdapterTour)
//     .constant('adapterTour2Constants', AdapterTour2)
//     .constant('adapterTour3Constants', AdapterTour3)
// 	.name;

import {NgModule} from "@angular/core";
import {RestApi} from "./rest-api.service";
import {AuthService} from "./auth.service";
import {ShepherdService} from "./tour/shepherd.service";
import {TourProviderService} from "./tour/tour-provider.service";
import {NotificationCountService} from "./notification-count-service";

@NgModule({
  imports: [],
  declarations: [],
  providers: [
    RestApi,
    AuthService,
    ShepherdService,
    TourProviderService,
    NotificationCountService,
  ],
  entryComponents: []
})
export class ServicesModule {
}
