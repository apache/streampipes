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
import { AdapterDescription } from '@streampipes/platform-services';
import { RestApi } from '../../../../services/rest-api.service';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { SpAdapterDocumentationDialogComponent } from '../../../dialog/adapter-documentation/adapter-documentation-dialog.component';

@Component({
    selector: 'sp-adapter-description',
    templateUrl: './adapter-description.component.html',
    styleUrls: ['./adapter-description.component.scss'],
})
export class AdapterDescriptionComponent implements OnInit {
    @Input()
    adapter: AdapterDescription;

    isRunningAdapter = false;
    adapterLabel: string;
    iconUrl: SafeUrl;

    constructor(
        private restApi: RestApi,
        private sanitizer: DomSanitizer,
        private dialogService: DialogService,
    ) {}

    ngOnInit() {
        if (this.adapter.name == null) {
            this.adapter.name = '';
        }
        this.isRunningAdapter =
            this.adapter.elementId !== undefined &&
            !(this.adapter as any).isTemplate;
        this.adapterLabel = this.adapter.name.split(' ').join('_');
        this.iconUrl = this.sanitizer.bypassSecurityTrustUrl(
            this.makeAssetIconUrl(),
        );
    }

    makeAssetIconUrl() {
        return this.restApi.getAssetUrl(this.adapter.appId) + '/icon';
    }

    openDocumentation(event: MouseEvent): void {
        event.stopPropagation();
        this.dialogService.open(SpAdapterDocumentationDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: 'Documentation',
            width: '50vw',
            data: {
                appId: this.adapter.appId,
            },
        });
    }
}
