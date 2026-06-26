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
import { DialogRef } from '@streampipes/shared-ui';
import { AssetLink, AssetLinkType } from '@streampipes/platform-services';
import { BaseAssetLinksDirective } from '../base-asset-links.directive';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';
import {
    AssetLinkResourceRow,
    AssetLinkSelectionChange,
} from './asset-link-table/asset-link-table.model';
import { AssetLinkTableComponent } from './asset-link-table/asset-link-table.component';

@Component({
    selector: 'sp-manage-asset-links-dialog-component',
    templateUrl: './manage-asset-links-dialog.component.html',
    imports: [
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        MatButton,
        MatDivider,
        AssetLinkTableComponent,
        TranslatePipe,
    ],
})
export class SpManageAssetLinksDialogComponent
    extends BaseAssetLinksDirective
    implements OnInit
{
    private dialogRef =
        inject<DialogRef<SpManageAssetLinksDialogComponent>>(DialogRef);

    @Input()
    assetLinks: AssetLink[];

    @Input()
    assetLinkTypes: AssetLinkType[];

    clonedAssetLinks: AssetLink[] = [];

    resourceRows: AssetLinkResourceRow[] = [];

    ngOnInit(): void {
        super.onInit();
        this.clonedAssetLinks = [
            ...this.assetLinks.map(al => {
                return { ...al };
            }),
        ];
    }

    get selectedResourceIds(): string[] {
        return this.clonedAssetLinks.map(assetLink => assetLink.resourceId);
    }

    cancel(): void {
        this.dialogRef.close();
    }

    store(): void {
        this.assetLinks = this.clonedAssetLinks;
        this.dialogRef.close(this.assetLinks);
    }

    afterResourcesLoaded(): void {
        this.resourceRows = [
            ...this.adapters.map(adapter =>
                this.makeResourceRow(
                    adapter.elementId,
                    adapter.name,
                    'Adapter',
                    'adapter',
                ),
            ),
            ...this.charts.map(chart =>
                this.makeResourceRow(
                    chart.elementId,
                    chart.baseAppearanceConfig.widgetTitle,
                    'Chart',
                    'chart',
                ),
            ),
            ...this.dashboards.map(dashboard =>
                this.makeResourceRow(
                    dashboard.elementId,
                    dashboard.name,
                    'Dashboard',
                    'dashboard',
                ),
            ),
            ...this.dataLakeMeasures.map(measure =>
                this.makeResourceRow(
                    measure.elementId,
                    measure.measureName,
                    'Data Lake Storage',
                    'measurement',
                ),
            ),
            ...this.dataSources.map(source =>
                this.makeResourceRow(
                    source.elementId,
                    source.name,
                    'Data Stream',
                    'data-source',
                ),
            ),
            ...this.files.map(file =>
                this.makeResourceRow(
                    file.fileId,
                    file.filename,
                    'File',
                    'file',
                ),
            ),
            ...this.pipelines.map(pipeline =>
                this.makeResourceRow(
                    pipeline.elementId,
                    pipeline.name,
                    'Pipeline',
                    'pipeline',
                ),
            ),
        ];
    }

    linkSelected(resourceId: string): boolean {
        return (
            this.clonedAssetLinks.find(al => al.resourceId === resourceId) !==
            undefined
        );
    }

    selectLink(event: AssetLinkSelectionChange): void {
        const resource = event.resource;
        if (event.checked) {
            if (this.linkSelected(resource.resourceId)) {
                return;
            }

            this.clonedAssetLinks.push(
                this.makeLink(
                    resource.resourceId,
                    resource.resourceName,
                    resource.assetLinkType,
                ),
            );
        } else {
            const index = this.clonedAssetLinks.findIndex(
                al => al.resourceId === resource.resourceId,
            );
            if (index > -1) {
                this.clonedAssetLinks.splice(index, 1);
            }
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

    private makeResourceRow(
        resourceId: string,
        resourceName: string,
        resourceType: string,
        assetLinkType: string,
    ): AssetLinkResourceRow {
        return {
            resourceId,
            resourceName,
            resourceType,
            assetLinkType,
        };
    }
}
