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
    AfterViewInit,
    Component,
    inject,
    Input,
    OnChanges,
    OnInit,
    SimpleChanges,
    ViewChild,
} from '@angular/core';
import {
    AssetLink,
    AssetLinkType,
    SpAsset,
    SpAssetModel,
} from '@streampipes/platform-services';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { EditAssetLinkDialogComponent } from '../../../../../dialog/edit-asset-link/edit-asset-link-dialog.component';
import { DialogService, PanelType } from '@streampipes/shared-ui';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';

@Component({
    selector: 'sp-asset-link-table',
    templateUrl: './asset-link-table.component.html',
    standalone: false,
})
export class AssetLinkTableComponent
    implements OnInit, AfterViewInit, OnChanges
{
    @Input()
    assetModel: SpAssetModel;

    @Input()
    asset: SpAsset;

    @Input()
    assetLinkTypes: AssetLinkType[];

    @Input()
    editMode = false;

    @ViewChild(MatSort)
    sort: MatSort;

    displayedColumns: string[] = [
        'type',
        'linkLabel',
        'additionalInfo',
        'actions',
    ];

    dataSource: MatTableDataSource<AssetLink> = new MatTableDataSource();

    private dialogService = inject(DialogService);
    private translateService = inject(TranslateService);
    private router = inject(Router);

    ngOnInit() {
        this.dataSource.sortingDataAccessor = (link, column) => {
            if (column === 'type') {
                return link.linkType;
            }
            return link[column];
        };
    }

    ngOnChanges(changes: SimpleChanges) {
        this.refreshData();
    }

    ngAfterViewInit() {
        setTimeout(() => {
            this.dataSource.sort = this.sort;
        });
    }

    refreshData(): void {
        this.dataSource.data = this.asset.assetLinks;
    }

    openDetails(assetLink: AssetLink) {}

    navigate(assetLink: AssetLink) {
        const linkType = this.assetLinkTypes.find(
            l => l.linkType === assetLink.linkType,
        );
        if (!linkType?.navPaths?.length) {
            return;
        }
        this.router.navigate([...linkType.navPaths, assetLink.resourceId]);
    }

    openEditAssetLinkDialog(assetLink: AssetLink): void {
        const index = this.asset.assetLinks.indexOf(assetLink);
        const dialogRef = this.dialogService.open(
            EditAssetLinkDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: this.translateService.instant('Update asset links'),
                width: '50vw',
                data: {
                    assetLink: assetLink,
                    assetLinkTypes: this.assetLinkTypes,
                    createMode: false,
                },
            },
        );

        dialogRef.afterClosed().subscribe(storedLink => {
            if (storedLink) {
                this.asset.assetLinks[index] = storedLink;
                this.asset.assetLinks = [...this.asset.assetLinks];
                this.refreshData();
            }
        });
    }

    deleteAssetLink(assetLink: AssetLink): void {
        const index = this.asset.assetLinks.indexOf(assetLink);
        this.asset.assetLinks.splice(index, 1);
        this.asset.assetLinks = [...this.asset.assetLinks];
        this.refreshData();
    }
}
