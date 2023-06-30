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
import { BaseAssetLinksDirective } from '../base-asset-links.directive';

@Component({
    selector: 'sp-manage-asset-links-dialog-component',
    templateUrl: './manage-asset-links-dialog.component.html',
    styleUrls: ['./manage-asset-links-dialog.component.scss'],
})
export class SpManageAssetLinksDialogComponent
    extends BaseAssetLinksDirective
    implements OnInit
{
    @Input()
    assetLinks: AssetLink[];

    @Input()
    assetLinkTypes: AssetLinkType[];

    clonedAssetLinks: AssetLink[] = [];

    idFunction = el => el._id;
    elementIdFunction = el => el.elementId;
    fileIdFunction = el => el.fileId;
    nameFunction = el => el.name;
    filenameFunction = el => el.originalFilename;
    measureNameFunction = el => el.measureName;

    constructor(
        private dialogRef: DialogRef<SpManageAssetLinksDialogComponent>,
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
        this.clonedAssetLinks = [
            ...this.assetLinks.map(al => {
                return { ...al };
            }),
        ];
    }

    cancel(): void {
        this.dialogRef.close();
    }

    store(): void {
        this.assetLinks = this.clonedAssetLinks;
        this.dialogRef.close(this.assetLinks);
    }

    afterResourcesLoaded(): void {}

    linkSelected(resourceId: string): boolean {
        return (
            this.clonedAssetLinks.find(al => al.resourceId === resourceId) !==
            undefined
        );
    }

    selectLink(
        checked: boolean,
        resourceId: string,
        label: string,
        assetLinkType: string,
    ): void {
        if (checked) {
            this.clonedAssetLinks.push(
                this.makeLink(resourceId, label, assetLinkType),
            );
        } else {
            const index = this.clonedAssetLinks.findIndex(
                al => al.resourceId === resourceId,
            );
            this.clonedAssetLinks.splice(index, 1);
        }
    }

    makeLink(
        resourceId: string,
        label: string,
        assetLinkType: string,
    ): AssetLink {
        const linkType = this.assetLinkTypes.find(
            a => a.linkType === assetLinkType,
        );
        return {
            linkLabel: label,
            linkType: linkType.linkType,
            editingDisabled: false,
            queryHint: linkType.linkQueryHint,
            navigationActive: linkType.navigationActive,
            resourceId,
        };
    }

    selectAll(
        elements: any[],
        idFunction: any,
        nameFunction: any,
        assetLinkType: string,
    ): void {
        elements.forEach(el => {
            const id = idFunction(el);
            const elementName = nameFunction(el);
            if (!this.linkSelected(id)) {
                this.selectLink(true, id, elementName, assetLinkType);
            }
        });
    }

    deselectAll(elements: any[], idFunction: any): void {
        elements.forEach(el => {
            const id = idFunction(el);
            const index = this.clonedAssetLinks.findIndex(
                al => al.resourceId === id,
            );
            if (index > -1) {
                this.clonedAssetLinks.splice(index, 1);
            }
        });
    }
}
