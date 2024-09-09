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

import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SpConfigurationTabsService } from './configuration-tabs.service';

export const configurationRouteGuard: CanActivateFn = route => {
    const tabService = inject(SpConfigurationTabsService);
    const tabs = tabService.getTabs();
    const router: Router = inject(Router);
    const path = route.routeConfig.path;
    if (tabService.isTabActive(tabs, path)) {
        return true;
    } else {
        router.navigate(tabs[0].itemLink, { skipLocationChange: true });
        return false;
    }
};
