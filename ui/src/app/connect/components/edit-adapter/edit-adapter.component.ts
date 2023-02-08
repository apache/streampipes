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
import { ActivatedRoute } from '@angular/router';

import {
    AdapterDescriptionUnion,
    AdapterService,
} from '@streampipes/platform-services';
import { SpBreadcrumbService } from '@streampipes/shared-ui';
import { SpConnectRoutes } from '../../connect.routes';

@Component({
    selector: 'sp-edit-adapter',
    templateUrl: './edit-adapter.component.html',
    styleUrls: ['./edit-adapter.component.scss'],
})
export class EditAdapterComponent implements OnInit {
    initialized = false;
    adapterName = '';
    adapter: AdapterDescriptionUnion = undefined;

    constructor(
        private adapterService: AdapterService,
        private breadcrumbService: SpBreadcrumbService,
        private route: ActivatedRoute,
    ) {}

    ngOnInit(): void {
        this.adapterService
            .getAdapter(this.route.snapshot.params.elementId)
            .subscribe(adapter => {
                this.adapter = adapter;
                this.adapterName = adapter.name;
                this.initialized = true;

                this.breadcrumbService.updateBreadcrumb(
                    this.breadcrumbService.makeRoute(
                        [SpConnectRoutes.BASE, SpConnectRoutes.EDIT],
                        this.adapterName,
                    ),
                );
            });
    }
}
