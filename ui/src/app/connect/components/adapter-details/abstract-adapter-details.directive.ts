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

import { AuthService } from '../../../services/auth.service';
import { ActivatedRoute } from '@angular/router';
import {
    AdapterDescriptionUnion,
    AdapterService,
    AdapterMonitoringService,
} from '@streampipes/platform-services';
import { SpNavigationItem } from '@streampipes/shared-ui';
import { SpAdapterDetailsTabs } from './adapter-details-tabs';
import { SpBreadcrumbService } from '../../../../../projects/streampipes/shared-ui/src/lib/services/breadcrumb.service';

export abstract class SpAbstractAdapterDetailsDirective {
    currentAdapterId: string;
    tabs: SpNavigationItem[] = [];
    adapter: AdapterDescriptionUnion;

    constructor(
        protected authService: AuthService,
        protected activatedRoute: ActivatedRoute,
        protected adapterService: AdapterService,
        protected adapterMonitoringService: AdapterMonitoringService,
        protected breadcrumbService: SpBreadcrumbService,
    ) {}

    onInit(): void {
        this.authService.user$.subscribe(user => {
            const elementId = this.activatedRoute.snapshot.params.elementId;
            if (elementId) {
                this.tabs = new SpAdapterDetailsTabs().getTabs(elementId);
                this.currentAdapterId = elementId;
                this.loadAdapter();
            }
        });
    }

    loadAdapter(): void {
        this.adapterService.getAdapter(this.currentAdapterId).subscribe(res => {
            this.adapter = res;
            this.onAdapterLoaded();
        });
    }

    triggerUpdate(): void {
        this.adapterMonitoringService
            .triggerMonitoringUpdate()
            .subscribe(() => {
                this.onAdapterLoaded();
            });
    }

    abstract onAdapterLoaded(): void;
}
