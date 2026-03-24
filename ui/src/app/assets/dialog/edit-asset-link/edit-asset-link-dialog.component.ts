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

import { Component, Input, OnInit, inject } from '@angular/core';
import { DialogRef, FormFieldComponent } from '@streampipes/shared-ui';
import {
    AdapterService,
    AssetLink,
    AssetLinkType,
    ChartService,
    DashboardService,
    DatalakeRestService,
    FilesService,
    GenericStorageService,
    PipelineElementService,
    PipelineService,
} from '@streampipes/platform-services';
import { FormsModule, UntypedFormGroup } from '@angular/forms';
import {
    MatOption,
    MatSelect,
    MatSelectChange,
} from '@angular/material/select';
import { BaseAssetLinksDirective } from '../base-asset-links.directive';
import { FlexDirective, LayoutDirective } from '@ngbracket/ngx-layout/flex';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatDivider } from '@angular/material/divider';
import { MatButton } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-edit-asset-link-dialog-component',
    templateUrl: './edit-asset-link-dialog.component.html',
    imports: [
        FlexDirective,
        LayoutDirective,
        FormFieldComponent,
        MatFormField,
        MatSelect,
        MatOption,
        FormsModule,
        MatInput,
        MatDivider,
        MatButton,
        TranslatePipe,
    ],
})
export class EditAssetLinkDialogComponent
    extends BaseAssetLinksDirective
    implements OnInit
{
    private dialogRef =
        inject<DialogRef<EditAssetLinkDialogComponent>>(DialogRef);
    protected genericStorageService: GenericStorageService;
    protected pipelineService: PipelineService;
    protected chartService: ChartService;
    protected dashboardService: DashboardService;
    protected dataLakeService: DatalakeRestService;
    protected pipelineElementService: PipelineElementService;
    protected adapterService: AdapterService;
    protected filesService: FilesService;

    @Input()
    assetLink: AssetLink;

    @Input()
    assetLinkTypes: AssetLinkType[];

    @Input()
    createMode: boolean;

    parentForm: UntypedFormGroup;

    clonedAssetLink: AssetLink;

    currentResource: any;

    selectedLinkType: AssetLinkType;

    constructor() {
        const genericStorageService = inject(GenericStorageService);
        const pipelineService = inject(PipelineService);
        const chartService = inject(ChartService);
        const dashboardService = inject(DashboardService);
        const dataLakeService = inject(DatalakeRestService);
        const pipelineElementService = inject(PipelineElementService);
        const adapterService = inject(AdapterService);
        const filesService = inject(FilesService);

        super(
            genericStorageService,
            pipelineService,
            chartService,
            dashboardService,
            dataLakeService,
            pipelineElementService,
            adapterService,
            filesService,
        );

        this.genericStorageService = genericStorageService;
        this.pipelineService = pipelineService;
        this.chartService = chartService;
        this.dashboardService = dashboardService;
        this.dataLakeService = dataLakeService;
        this.pipelineElementService = pipelineElementService;
        this.adapterService = adapterService;
        this.filesService = filesService;
    }

    ngOnInit(): void {
        super.onInit();
        this.clonedAssetLink = { ...this.assetLink };
        this.selectedLinkType = this.getCurrAssetLinkType();
    }

    getCurrAssetLinkType(): AssetLinkType {
        return this.assetLinkTypes.find(
            a => a.linkType === this.clonedAssetLink.linkType,
        );
    }

    store() {
        this.assetLink = this.clonedAssetLink;
        this.dialogRef.close(this.assetLink);
    }

    cancel() {
        this.dialogRef.close();
    }

    onLinkTypeChanged(event: MatSelectChange): void {
        this.selectedLinkType = event.value;
        const linkType = this.assetLinkTypes.find(
            a => a.linkType === this.selectedLinkType.linkType,
        );
        this.clonedAssetLink.editingDisabled = false;
        this.clonedAssetLink.linkType = linkType.linkType;
        this.clonedAssetLink.queryHint = linkType.linkQueryHint;
        this.clonedAssetLink.navigationActive = linkType.navigationActive;
    }

    changeLabel(id: string, label: string, currentResource: any) {
        this.clonedAssetLink.resourceId = id;
        this.clonedAssetLink.linkLabel = label;
        this.currentResource = currentResource;
    }

    afterResourcesLoaded(): void {
        if (!this.createMode) {
            this.currentResource = this.allResources.find(
                r => r.elementId === this.clonedAssetLink.resourceId,
            );
        }
    }
}
