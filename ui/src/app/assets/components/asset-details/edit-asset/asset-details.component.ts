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

import { Component, inject } from '@angular/core';
import {
    ConfirmDialogComponent,
    DialogService,
    ObjectManageDialogComponent,
    ObjectManageDialogResourceConfig,
    ObjectManageDialogResult,
    PanelType,
    SpAssetBrowserService,
    SpBasicViewComponent,
} from '@streampipes/shared-ui';
import { Router } from '@angular/router';
import { BaseAssetDetailsDirective } from '../base-asset-details.directive';
import { SpAssetSelectionPanelComponent } from './asset-selection-panel/asset-selection-panel.component';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton, MatIconButton } from '@angular/material/button';
import { AssetDetailsBasicsComponent } from './asset-details-panel/asset-details-basics/asset-details-basics.component';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatIcon } from '@angular/material/icon';
import {
    SpAssetModel,
    PermissionsService,
} from '@streampipes/platform-services';
import { MatDialog } from '@angular/material/dialog';
import { firstValueFrom } from 'rxjs';

type ManageableAsset = SpAssetModel & {
    name: string;
    description: string;
};

@Component({
    selector: 'sp-asset-details',
    templateUrl: './asset-details.component.html',
    imports: [
        SpAssetSelectionPanelComponent,
        SpBasicViewComponent,
        FlexDirective,
        LayoutAlignDirective,
        LayoutDirective,
        MatButton,
        MatIconButton,
        MatMenuTrigger,
        MatMenu,
        MatMenuItem,
        MatIcon,
        AssetDetailsBasicsComponent,
        TranslatePipe,
    ],
})
export class SpAssetDetailsComponent extends BaseAssetDetailsDirective {
    private router = inject(Router);
    private assetBrowserService = inject(SpAssetBrowserService);
    private dialog = inject(MatDialog);
    private dialogService = inject(DialogService);
    private translateService = inject(TranslateService);
    private permissionsService = inject(PermissionsService);

    private pendingManageAssetResult?: ObjectManageDialogResult<ManageableAsset>;

    async saveAsset() {
        this.cleanupEmpty();
        await firstValueFrom(this.assetService.updateAsset(this.asset));
        await this.savePendingManageAssetChanges();
        this.assetBrowserService.refreshBrowserAssetData();
        this.router.navigate(['assets']);
    }

    cleanupEmpty(): void {
        if (this.asset.additionalData?.customFields) {
            this.asset.additionalData!.customFields =
                this.asset.additionalData.customFields.filter(
                    f => f.key?.trim() || f.value?.trim(),
                );
        }
    }

    manageAsset(): void {
        const resource = this.makeManageableAsset(this.asset);
        const resourceConfig: ObjectManageDialogResourceConfig<ManageableAsset> =
            {
                resourceLabel: 'Asset',
                nameLabel: 'Asset name',
                descriptionLabel: 'Asset description',
                nameProperty: 'name',
                showAssetLinking: false,
            };
        const dialogRef = this.dialogService.open(ObjectManageDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.translateService.instant('Manage'),
            width: '50vw',
            data: {
                objectInstanceId: resource.elementId,
                resource,
                saveMode: 'deferred',
                resourceConfig,
                headerTitle:
                    this.translateService.instant('Manage Asset ') +
                    resource.name,
            },
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result && typeof result !== 'boolean') {
                this.pendingManageAssetResult = result;
                Object.assign(
                    this.asset,
                    this.makeAssetResource(result.resource),
                );
            }
        });
    }

    deleteAsset(): void {
        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            data: {
                title: this.translateService.instant(
                    'Are you sure you want to delete this asset?',
                ),
                subtitle: this.translateService.instant(
                    'This action cannot be reversed!',
                ),
                cancelTitle: this.translateService.instant('Cancel'),
                confirmTitle: this.translateService.instant('Delete Asset'),
            },
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result === 'confirm') {
                this.assetService
                    .deleteAsset(this.asset.elementId)
                    .subscribe(() => {
                        this.assetBrowserService.refreshBrowserAssetData();
                        this.router.navigate(['assets']);
                    });
            }
        });
    }

    onAssetAvailable() {}

    private makeManageableAsset(asset: SpAssetModel): ManageableAsset {
        return {
            ...asset,
            name: asset.assetName,
            description: asset.assetDescription,
        };
    }

    private makeAssetResource(resource: ManageableAsset): SpAssetModel {
        const { name, description, ...asset } = resource;
        return {
            ...asset,
            assetName: name,
            assetDescription: description,
        };
    }

    private async savePendingManageAssetChanges(): Promise<void> {
        const result = this.pendingManageAssetResult;
        if (!result) {
            return;
        }

        if (result.permission) {
            await firstValueFrom(
                this.permissionsService.updatePermission(result.permission),
            );
        }

        this.pendingManageAssetResult = undefined;
    }
}
