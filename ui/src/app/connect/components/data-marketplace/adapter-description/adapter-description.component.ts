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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ConnectService } from '../../../services/connect.service';
import { MatDialog } from '@angular/material/dialog';
import {
    AdapterDescription,
    AdapterService,
} from '@streampipes/platform-services';
import { DialogService } from '@streampipes/shared-ui';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
    selector: 'sp-adapter-description',
    templateUrl: './adapter-description.component.html',
    styleUrls: ['./adapter-description.component.scss'],
})
export class AdapterDescriptionComponent implements OnInit {
    @Input()
    adapter: AdapterDescription;

    @Output()
    updateAdapterEmitter: EventEmitter<void> = new EventEmitter<void>();

    @Output()
    createTemplateEmitter: EventEmitter<AdapterDescription> =
        new EventEmitter<AdapterDescription>();

    className = '';
    isRunningAdapter = false;
    adapterLabel: string;

    constructor(
        private connectService: ConnectService,
        private dataMarketplaceService: AdapterService,
        private dialogService: DialogService,
        public dialog: MatDialog,
        private _snackBar: MatSnackBar,
    ) {}

    ngOnInit() {
        if (this.adapter.name == null) {
            this.adapter.name = '';
        }
        this.isRunningAdapter =
            this.adapter.elementId !== undefined &&
            !(this.adapter as any).isTemplate;
        this.adapterLabel = this.adapter.name.split(' ').join('_');
        this.className = this.getClassName();
    }

    getClassName() {
        let className = this.isRunningAdapter
            ? 'adapter-box'
            : 'adapter-description-box';

        className += ' adapter-box-stream';

        return className;
    }

    getIconUrl() {
        // TODO Use "this.adapter.includesAssets" if boolean demoralizing is working
        if (this.adapter.includedAssets.length > 0) {
            return (
                this.dataMarketplaceService.getAssetUrl(this.adapter.appId) +
                '/icon'
            );
        } else {
            return `assets/img/connect/${this.adapter.iconUrl}`;
        }
    }

    removeAdapter(): void {
        this.dataMarketplaceService
            .deleteAdapterDescription(this.adapter.elementId)
            .subscribe({
                next: () => {
                    this.updateAdapterEmitter.emit();
                },
                error: () => {
                    this._snackBar.open(
                        'Cannot delete an adapter which has an active instance running.',
                    );
                },
            });
    }
}
