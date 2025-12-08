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

import {
    Component,
    EventEmitter,
    Input,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import {
    AssetConstants,
    AssetLink,
    AssetLinkType,
    GenericStorageService,
    SpAsset,
    SpAssetModel,
} from '@streampipes/platform-services';
import { SpManageAssetLinksDialogComponent } from '../../../../../dialog/manage-asset-links/manage-asset-links-dialog.component';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { EditAssetLinkDialogComponent } from '../../../../../dialog/edit-asset-link/edit-asset-link-dialog.component';
import { TranslateService } from '@ngx-translate/core';
import { AssetLinkTableComponent } from '../../../view-asset/view-asset-links/asset-link-table/asset-link-table.component';

@Component({
    selector: 'sp-asset-details-links',
    templateUrl: './asset-details-links.component.html',
    standalone: false,
})
export class AssetDetailsLinksComponent implements OnInit {
    @Input()
    asset: SpAsset;

    @Input()
    assetModel: SpAssetModel;

    @Input()
    editMode: boolean;

    @Output()
    updateAssetEmitter: EventEmitter<SpAsset> = new EventEmitter<SpAsset>();

    assetLinkTypes: AssetLinkType[] = [];
    assetLinksLoaded = false;

    @ViewChild('assetLinkTable', { static: false })
    assetLinkTable: AssetLinkTableComponent;

    constructor(
        private genericStorageService: GenericStorageService,
        private dialogService: DialogService,
        private translateService: TranslateService,
    ) {}

    ngOnInit(): void {
        this.genericStorageService
            .getAllDocuments(AssetConstants.ASSET_LINK_TYPES_DOC_NAME)
            .subscribe(assetLinkTypes => {
                this.assetLinkTypes = assetLinkTypes.sort((a, b) =>
                    a.linkLabel.localeCompare(b.linkLabel),
                );
                this.assetLinksLoaded = true;
            });
    }

    openManageAssetLinksDialog(): void {
        const dialogRef = this.dialogService.open(
            SpManageAssetLinksDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: this.translateService.instant('Manage asset links'),
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
                this.assetLinkTable?.refreshData();
            }
        });
    }

    openCreateAssetLinkDialog(): void {
        const assetLink: AssetLink = {
            linkLabel: '',
            linkType: 'chart',
            editingDisabled: false,
            resourceId: '',
            navigationActive: true,
            queryHint: 'chart',
        };
        const dialogRef = this.dialogService.open(
            EditAssetLinkDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: this.translateService.instant('Create asset links'),
                width: '50vw',
                data: {
                    assetLink: assetLink,
                    assetLinkTypes: this.assetLinkTypes,
                    createMode: true,
                },
            },
        );

        dialogRef.afterClosed().subscribe(storedLink => {
            if (storedLink) {
                this.asset.assetLinks.push(storedLink);
                this.asset.assetLinks = [...this.asset.assetLinks];
                this.assetLinkTable?.refreshData();
            }
        });
    }
}
