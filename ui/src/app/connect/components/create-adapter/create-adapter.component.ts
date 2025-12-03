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
import {
    AdapterDescription,
    AdapterService,
} from '@streampipes/platform-services';
import { ActivatedRoute } from '@angular/router';
import { SpConnectRoutes } from '../../connect.routes';
import { SpBreadcrumbService } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-create-adapter',
    templateUrl: './create-adapter.component.html',
    styleUrls: ['./create-adapter.component.scss'],
    standalone: false,
})
export class CreateAdapterComponent implements OnInit {
    initialized = false;
    adapterTypeName = '';
    adapter: AdapterDescription = undefined;

    constructor(
        private breadcrumbService: SpBreadcrumbService,
        private adapterService: AdapterService,
        private route: ActivatedRoute,
    ) {}

    ngOnInit(): void {
        this.adapterService.getAdapterDescriptions().subscribe(adapters => {
            const adapter = this.findAdapterWithAppIdFromRoute(adapters);

            this.updateAdapterTypeAndBreadcrumb(adapter);

            this.initializeAdapterInstance(adapter);
        });
    }

    private findAdapterWithAppIdFromRoute(
        adapters: AdapterDescription[],
    ): AdapterDescription {
        const appId = this.route.snapshot.params.appId;
        return adapters.find(a => a.appId === appId);
    }

    private initializeAdapterInstance(adapter: AdapterDescription) {
        this.adapter = this.copyAdapter(adapter);
        this.adapter.name = '';
        this.adapter.description = '';
        this.initialized = true;
    }

    /**
     * Create a copy of the adapter description.
     * Input is the adapter description with the app id and output is the new instance.
     */
    private copyAdapter(adapter: AdapterDescription): AdapterDescription {
        return AdapterDescription.fromData(adapter);
    }

    private updateAdapterTypeAndBreadcrumb(adapter: AdapterDescription) {
        this.adapterTypeName = adapter.name;
        this.breadcrumbService.updateBreadcrumb(
            this.breadcrumbService.makeRoute(
                [SpConnectRoutes.BASE, SpConnectRoutes.CREATE],
                this.adapterTypeName,
            ),
        );
    }
}
