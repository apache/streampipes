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
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { FlexLayoutModule } from '@ngbracket/ngx-layout';
import { CustomMaterialModule } from '../CustomMaterial/custom-material.module';
import { FormsModule } from '@angular/forms';
import { NotificationsComponent } from './notifications.component';
import { NotificationItemComponent } from './components/notification-item.component';
import { NotificationsService } from './service/notifications.service';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { PlatformServicesModule } from '@streampipes/platform-services';

@NgModule({
    imports: [
        CommonModule,
        MatProgressSpinnerModule,
        MatTabsModule,
        FlexLayoutModule,
        CommonModule,
        FlexLayoutModule,
        CustomMaterialModule,
        FormsModule,
        PlatformServicesModule,
    ],
    declarations: [NotificationsComponent, NotificationItemComponent],
    providers: [NotificationsService],
    exports: [NotificationsComponent],
})
export class NotificationModule {
    constructor() {}
}
