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

import { NgModule } from '@angular/core';
import { RestApi } from './rest-api.service';
import { AuthService } from './auth.service';
import { ShepherdService } from './tour/shepherd.service';
import { TourProviderService } from './tour/tour-provider.service';
import { NotificationCountService } from './notification-count-service';
import { PropertySelectorService } from './property-selector.service';
import { ElementIconText } from './get-element-icon-text.service';
import { AppConstants } from './app.constants';
import { SecurePipe } from './secure.pipe';

@NgModule({
    imports: [],
    declarations: [SecurePipe],
    providers: [
        AppConstants,
        RestApi,
        AuthService,
        ElementIconText,
        ShepherdService,
        TourProviderService,
        NotificationCountService,
        PropertySelectorService,
        SecurePipe,
    ],
    exports: [SecurePipe],
})
export class ServicesModule {}
