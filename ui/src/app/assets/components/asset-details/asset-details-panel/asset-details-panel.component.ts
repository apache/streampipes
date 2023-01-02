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
import {
    AssetLink,
    AssetLinkType,
    GenericStorageService,
    SpAsset,
} from '@streampipes/platform-services';
import { AssetConstants } from '../../../constants/asset.constants';
import { AssetUploadDialogComponent } from '../../../dialog/asset-upload/asset-upload-dialog.component';
import {
    DialogService,
    PanelType,
} from '../../../../../../dist/streampipes/shared-ui';
import { EditAssetLinkDialogComponent } from '../../../dialog/edit-asset-link/edit-asset-link-dialog.component';
import { SpManageAssetLinksDialogComponent } from '../../../dialog/manage-asset-links/manage-asset-links-dialog.component';

@Component({
    selector: 'sp-asset-details-panel-component',
    templateUrl: './asset-details-panel.component.html',
    styleUrls: ['./asset-details-panel.component.scss'],
})
export class SpAssetDetailsPanelComponent implements OnInit {
    @Input()
    asset: SpAsset;

    @Input()
    editMode: boolean;

    @Output()
    updateAssetEmitter: EventEmitter<SpAsset> = new EventEmitter<SpAsset>();

    assetLinkTypes: AssetLinkType[];

    constructor(
        private genericStorageService: GenericStorageService,
        private dialogService: DialogService,
    ) {}

    ngOnInit(): void {
        this.genericStorageService
            .getAllDocuments(AssetConstants.ASSET_LINK_TYPES_DOC_NAME)
            .subscribe(assetLinkTypes => {
                this.assetLinkTypes = assetLinkTypes.sort((a, b) =>
                    a.linkLabel.localeCompare(b.linkLabel),
                );
            });
    }

    openManageAssetLinksDialog(): void {
        const dialogRef = this.dialogService.open(
            SpManageAssetLinksDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: 'Manage asset links',
                width: '50vw',
                data: {
                    assetLinks: this.asset.assetLinks,
                    assetLinkTypes: this.assetLinkTypes,
                },
            },
        );

        dialogRef.afterClosed().subscribe(assetLinks => {
            if (assetLinks) {
                this.asset.assetLinks = assetLinks;
            }
        });
    }

    openEditAssetLinkDialog(
        assetLink: AssetLink,
        index: number,
        createMode: boolean,
    ): void {
        const dialogRef = this.dialogService.open(
            EditAssetLinkDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: createMode ? 'Create ' : 'Update ' + 'asset model',
                width: '50vw',
                data: {
                    assetLink: assetLink,
                    assetLinkTypes: this.assetLinkTypes,
                    createMode: createMode,
                },
            },
        );

        dialogRef.afterClosed().subscribe(storedLink => {
            if (storedLink) {
                if (index > -1) {
                    this.asset.assetLinks[index] = storedLink;
                } else {
                    this.asset.assetLinks.push(storedLink);
                }
                this.updateAssetEmitter.emit(this.asset);
            }
        });
    }

    openCreateAssetLinkDialog(): void {
        const assetLink: AssetLink = {
            linkLabel: '',
            linkType: 'data-view',
            editingDisabled: false,
            resourceId: '',
            navigationActive: true,
            queryHint: 'data-view',
        };
        this.openEditAssetLinkDialog(assetLink, -1, true);
    }

    deleteAssetLink(index: number): void {
        this.asset.assetLinks.splice(index, 1);
        this.updateAssetEmitter.emit(this.asset);
    }
}
