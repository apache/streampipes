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

import { DialogRef } from '@streampipes/shared-ui';
import {
    AdapterDescription,
    AdapterService,
} from '@streampipes/platform-services';
import { Component, Input } from '@angular/core';

@Component({
    selector: 'sp-adapter-notifications-dialog',
    templateUrl: './adapter-notifications-dialog.component.html',
    styleUrls: ['./adapter-notifications-dialog.component.scss'],
})
export class AdapterNotificationsDialogComponent {
    @Input()
    adapter: AdapterDescription;

    constructor(
        private dialogRef: DialogRef<AdapterNotificationsDialogComponent>,
        private adapterService: AdapterService,
    ) {}

    acknowledgeAndClose() {
        this.adapter.adapterNotifications = [];
        if (this.adapter.healthStatus === 'REQUIRES_ATTENTION') {
            this.adapter.healthStatus = 'OK';
        }
        this.adapterService.updateAdapter(this.adapter).subscribe(msg => {
            this.dialogRef.close();
        });
    }
}
