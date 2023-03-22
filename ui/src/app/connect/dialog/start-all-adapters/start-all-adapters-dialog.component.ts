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

import { Component, Input, OnInit } from '@angular/core';
import { DialogRef } from '@streampipes/shared-ui';
import {
    AdapterDescriptionUnion,
    AdapterService,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-start-all-adapters-dialog',
    templateUrl: './start-all-adapters-dialog.component.html',
    styleUrls: ['./start-all-adapters-dialog.component.scss'],
})
export class StartAllAdaptersDialogComponent implements OnInit {
    @Input()
    adapters: AdapterDescriptionUnion[];

    adaptersToModify: AdapterDescriptionUnion[];
    installationStatus: any;
    installationFinished: boolean;
    page: string;
    nextButton: string;
    installationRunning: boolean;

    @Input()
    action: boolean;

    constructor(
        private dialogRef: DialogRef<StartAllAdaptersDialogComponent>,
        private adapterService: AdapterService,
    ) {
        this.adaptersToModify = [];
        this.installationStatus = [];
        this.installationFinished = false;
        this.page = 'preview';
        this.nextButton = 'Next';
        this.installationRunning = false;
    }

    ngOnInit() {
        this.getAdaptersToModify();
        if (this.adaptersToModify.length === 0) {
            this.nextButton = 'Close';
            this.page = 'installation';
        }
    }

    close(refreshAdapters: boolean) {
        this.dialogRef.close(refreshAdapters);
    }

    next() {
        if (this.page === 'installation') {
            this.close(true);
        } else {
            this.page = 'installation';
            this.initiateInstallation(this.adaptersToModify[0], 0);
        }
    }

    getAdaptersToModify() {
        this.adapters.forEach(adapter => {
            this.adaptersToModify.push(adapter);
        });
    }

    initiateInstallation(adapter: AdapterDescriptionUnion, index) {
        this.installationRunning = true;
        this.installationStatus.push({
            name: adapter.name,
            id: index,
            status: 'waiting',
        });
        if (this.action) {
            this.startAdapter(adapter, index);
        } else {
            this.stopAdapter(adapter, index);
        }
    }

    startAdapter(adapter: AdapterDescriptionUnion, index) {
        this.adapterService
            .startAdapter(adapter)
            .subscribe(data => {
                this.installationStatus[index].status = data.success
                    ? 'success'
                    : 'error';
            })
            .add(() => {
                if (index < this.adaptersToModify.length - 1) {
                    index++;
                    this.initiateInstallation(
                        this.adaptersToModify[index],
                        index,
                    );
                } else {
                    this.nextButton = 'Close';
                    this.installationRunning = false;
                }
            });
    }

    stopAdapter(adapter: AdapterDescriptionUnion, index) {
        this.adapterService
            .stopAdapter(adapter)
            .subscribe(data => {
                this.installationStatus[index].status = data.success
                    ? 'success'
                    : 'error';
            })
            .add(() => {
                if (index < this.adaptersToModify.length - 1) {
                    index++;
                    this.initiateInstallation(
                        this.adaptersToModify[index],
                        index,
                    );
                } else {
                    this.nextButton = 'Close';
                    this.installationRunning = false;
                }
            });
    }
}
