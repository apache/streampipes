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
    AdapterDescription,
    AdapterService,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-start-all-adapters-dialog',
    templateUrl: './all-adapter-actions-dialog.component.html',
    styleUrls: ['./all-adapter-actions-dialog.component.scss'],
})
export class AllAdapterActionsComponent implements OnInit {
    @Input()
    adapters: AdapterDescription[];

    adaptersToModify: AdapterDescription[];
    actionStatus: any;
    actionFinished: boolean;
    page: string;
    nextButton: string;
    actionRunning: boolean;

    @Input()
    action: boolean;

    constructor(
        private dialogRef: DialogRef<AllAdapterActionsComponent>,
        private adapterService: AdapterService,
    ) {
        this.adaptersToModify = [];
        this.actionStatus = [];
        this.actionFinished = false;
        this.page = 'preview';
        this.nextButton = 'Next';
        this.actionRunning = false;
    }

    ngOnInit() {
        this.getAdaptersToModify();
        if (this.adaptersToModify.length === 0) {
            this.nextButton = 'Close';
            this.page = 'running';
        }
    }

    close(refreshAdapters: boolean) {
        this.dialogRef.close(refreshAdapters);
    }

    next() {
        if (this.page === 'running') {
            this.close(true);
        } else {
            this.page = 'running';
            this.initiateAction(this.adaptersToModify[0], 0);
        }
    }

    getAdaptersToModify() {
        this.adapters.forEach(adapter => {
            if (adapter.running != this.action) {
                this.adaptersToModify.push(adapter);
            }
        });
    }

    initiateAction(adapter: AdapterDescription, index) {
        this.actionRunning = true;
        this.actionStatus.push({
            name: adapter.name,
            id: index,
            status: 'waiting',
        });
        this.runAdapterAction(adapter, index);
    }

    runAdapterAction(adapter: AdapterDescription, index) {
        const observable = this.action
            ? this.adapterService.startAdapter(adapter)
            : this.adapterService.stopAdapter(adapter);
        observable
            .subscribe(data => {
                this.actionStatus[index].status = data.success
                    ? 'success'
                    : 'error';
            })
            .add(() => {
                if (index < this.adaptersToModify.length - 1) {
                    index++;
                    this.initiateAction(this.adaptersToModify[index], index);
                } else {
                    this.nextButton = 'Close';
                    this.actionRunning = false;
                }
            });
    }
}
