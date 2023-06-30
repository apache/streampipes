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
    AdapterService,
    AssetLink,
    AssetLinkType,
    DashboardService,
    DatalakeRestService,
    DataViewDataExplorerService,
    GenericStorageService,
    PipelineService,
    PipelineElementService,
    FilesService,
} from '@streampipes/platform-services';
import { UntypedFormGroup } from '@angular/forms';
import { MatSelectChange } from '@angular/material/select';
import { BaseAssetLinksDirective } from '../base-asset-links.directive';

@Component({
    selector: 'sp-edit-asset-link-dialog-component',
    templateUrl: './edit-asset-link-dialog.component.html',
    styleUrls: ['./edit-asset-link-dialog.component.scss'],
})
export class EditAssetLinkDialogComponent
    extends BaseAssetLinksDirective
    implements OnInit
{
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

    constructor(
        private dialogRef: DialogRef<EditAssetLinkDialogComponent>,
        protected genericStorageService: GenericStorageService,
        protected pipelineService: PipelineService,
        protected dataViewService: DataViewDataExplorerService,
        protected dashboardService: DashboardService,
        protected dataLakeService: DatalakeRestService,
        protected pipelineElementService: PipelineElementService,
        protected adapterService: AdapterService,
        protected filesService: FilesService,
    ) {
        super(
            genericStorageService,
            pipelineService,
            dataViewService,
            dashboardService,
            dataLakeService,
            pipelineElementService,
            adapterService,
            filesService,
        );
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
                r =>
                    r._id === this.clonedAssetLink.resourceId ||
                    r.elementId === this.clonedAssetLink.resourceId,
            );
        }
    }
}
