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

import { Component, inject, Input, OnInit } from '@angular/core';
import {
    AdapterDescription,
    PipelineElementAssetService,
} from '@streampipes/platform-services';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { SpAdapterDocumentationDialogComponent } from '../../../dialog/adapter-documentation/adapter-documentation-dialog.component';
import { Router } from '@angular/router';
import { ShepherdService } from '../../../../services/tour/shepherd.service';

@Component({
    selector: 'sp-adapter-catalog-item',
    templateUrl: './adapter-catalog-item.component.html',
    styleUrls: ['./adapter-catalog-item.component.scss'],
    standalone: false,
})
export class AdapterCatalogItemComponent implements OnInit {
    private pipelineElementAssetService = inject(PipelineElementAssetService);
    private sanitizer = inject(DomSanitizer);
    private dialogService = inject(DialogService);
    private router = inject(Router);
    private shepherdService = inject(ShepherdService);

    @Input()
    adapter: AdapterDescription;

    iconUrl: SafeUrl;

    ngOnInit() {
        if (this.adapter.name == null) {
            this.adapter.name = '';
        }

        this.iconUrl = this.sanitizer.bypassSecurityTrustUrl(
            this.makeAssetIconUrl(),
        );
    }

    makeAssetIconUrl() {
        return (
            this.pipelineElementAssetService.getAssetUrl(this.adapter.appId) +
            '/icon'
        );
    }

    selectAdapter(appId: string) {
        this.router.navigate(['connect', 'create', appId]).then(() => {
            this.shepherdService.trigger('new-adapter-selected');
        });
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
