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

import { Component, OnInit } from '@angular/core';
import { BaseNavigationComponent } from '../base-navigation.component';
import { Router } from '@angular/router';
import { NotificationCountService } from '../../../services/notification-count-service';
import { AuthService } from '../../../services/auth.service';
import { RestApi } from '../../../services/rest-api.service';
import { AppConstants } from '../../../services/app.constants';
import { CurrentUserService } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-iconbar',
    templateUrl: './iconbar.component.html',
    styleUrls: ['./iconbar.component.scss'],
})
export class IconbarComponent
    extends BaseNavigationComponent
    implements OnInit
{
    constructor(
        router: Router,
        authService: AuthService,
        currentUserService: CurrentUserService,
        public notificationCountService: NotificationCountService,
        private restApi: RestApi,
        appConstants: AppConstants,
    ) {
        super(authService, currentUserService, router, appConstants);
    }

    ngOnInit(): void {
        super.onInit();
    }
}
