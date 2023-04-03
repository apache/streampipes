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
import { ConnectService } from '../../services/connect.service';
import { SpConnectRoutes } from '../../connect.routes';
import { SpBreadcrumbService } from '@streampipes/shared-ui';

@Component({
    selector: 'sp-new-adapter',
    templateUrl: './new-adapter.component.html',
    styleUrls: ['./new-adapter.component.scss'],
})
export class NewAdapterComponent implements OnInit {
    initialized = false;
    adapterTypeName = '';
    adapter: AdapterDescription = undefined;

    constructor(
        private breadcrumbService: SpBreadcrumbService,
        private connectService: ConnectService,
        private adapterService: AdapterService,
        private route: ActivatedRoute,
    ) {}

    ngOnInit(): void {
        this.adapterService.getAdapterDescriptions().subscribe(adapters => {
            const adapter = adapters.find(
                a => a.appId === this.route.snapshot.params.appId,
            );
            this.adapterTypeName = adapter.name;
            this.adapter = AdapterDescription.fromData(adapter);

            this.breadcrumbService.updateBreadcrumb(
                this.breadcrumbService.makeRoute(
                    [SpConnectRoutes.BASE, SpConnectRoutes.CREATE],
                    this.adapterTypeName,
                ),
            );
            this.adapter.name = '';
            this.adapter.description = '';
            this.initialized = true;
        });
    }
}
