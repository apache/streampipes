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
    ConfirmDialogAction,
    ConfirmDialogComponent,
    DialogService,
    ObjectManageDialogComponent,
    ObjectManageDialogResourceConfig,
    ObjectManageDialogResult,
    PanelType,
    SpAssetBrowserService,
    SpBasicViewComponent,
} from '@streampipes/shared-ui';
import {
    ActivatedRouteSnapshot,
    Router,
    RouterStateSnapshot,
} from '@angular/router';
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
    PermissionsService,
    SpAssetModel,
} from '@streampipes/platform-services';
import { MatDialog } from '@angular/material/dialog';
import { firstValueFrom, from, Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { SupportsUnsavedChangeDialog } from '../../../../chart-shared/models/dataview-dashboard.model';

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
export class SpAssetDetailsComponent
    extends BaseAssetDetailsDirective
    implements SupportsUnsavedChangeDialog
{
    private router = inject(Router);
    private assetBrowserService = inject(SpAssetBrowserService);
    private dialog = inject(MatDialog);
    private dialogService = inject(DialogService);
    private translateService = inject(TranslateService);
    private permissionsService = inject(PermissionsService);

    private pendingManageAssetResult?: ObjectManageDialogResult<ManageableAsset>;
    private originalAsset: SpAssetModel;

    async saveAsset() {
        if (this.isNewAsset && this.pendingManageAssetResult === undefined) {
            this.openManageAssetDialog(true);
            return;
        }
        await this.saveAssetChanges();
        this.assetBrowserService.refreshBrowserAssetData();
        this.router.navigate(['assets'], {
            state: { omitConfirm: true },
        });
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
        this.openManageAssetDialog();
    }

    deleteAsset(): void {
        if (this.isNewAsset) {
            this.router.navigate(['assets'], {
                state: { omitConfirm: true },
            });
            return;
        }

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
                        this.router.navigate(['assets'], {
                            state: { omitConfirm: true },
                        });
                    });
            }
        });
    }

    private openManageAssetDialog(saveAfterClose = false): void {
        const resource = this.makeManageableAsset(this.asset);
        const resourceConfig: ObjectManageDialogResourceConfig<ManageableAsset> =
            {
                resourceLabel: 'Asset',
                nameLabel: 'Asset name',
                descriptionLabel: 'Asset description',
                nameProperty: 'name',
                showAssetLinking: false,
                saveResource: this.isNewAsset
                    ? resource =>
                          this.assetService
                              .createAsset(this.makeAssetResource(resource))
                              .pipe(
                                  tap(savedAsset => {
                                      Object.assign(
                                          this.asset,
                                          savedAsset ??
                                              this.makeAssetResource(resource),
                                      );
                                      this.isNewAsset = false;
                                  }),
                              )
                    : undefined,
            };
        const dialogRef = this.dialogService.open(ObjectManageDialogComponent, {
            panelType: PanelType.SLIDE_IN_PANEL,
            title: this.isNewAsset
                ? this.translateService.instant('New Asset')
                : this.translateService.instant('Manage'),
            width: '50vw',

            data: {
                objectInstanceId: resource.elementId,
                resource,
                saveMode: this.isNewAsset ? 'immediate' : 'deferred',
                createMode: this.isNewAsset,
                resourceConfig,
                headerTitle: this.isNewAsset
                    ? this.translateService.instant('New Asset')
                    : this.translateService.instant('Manage Asset ') +
                      (resource.name ?? ''),
            },
        });
        dialogRef.afterClosed().subscribe(result => {
            if (saveAfterClose && result === true) {
                this.assetBrowserService.refreshBrowserAssetData();
                this.router.navigate(['assets'], {
                    state: { omitConfirm: true },
                });
                return;
            }

            if (result && typeof result !== 'boolean') {
                this.pendingManageAssetResult = result;
                Object.assign(
                    this.asset,
                    this.makeAssetResource(result.resource),
                );

                if (saveAfterClose) {
                    void this.saveAsset();
                }
            }
        });
    }

    confirmLeaveDialog(
        _route: ActivatedRouteSnapshot,
        _state: RouterStateSnapshot,
    ): Observable<boolean> {
        if (this.setShouldShowConfirm()) {
            const dialogRef = this.dialog.open(ConfirmDialogComponent, {
                width: '500px',
                data: {
                    title: this.translateService.instant('Save changes?'),
                    subtitle: this.translateService.instant(
                        'Update all changes to asset or discard current changes.',
                    ),
                    neutralTitle: this.translateService.instant('Keep editing'),
                    cancelTitle:
                        this.translateService.instant('Discard changes'),
                    confirmTitle: this.translateService.instant('Update'),
                },
            });
            return dialogRef.afterClosed().pipe(
                switchMap((dialogResult: ConfirmDialogAction | undefined) => {
                    if (dialogResult === 'confirm') {
                        return from(this.saveAssetChanges()).pipe(
                            map(() => true),
                        );
                    }

                    if (dialogResult === 'cancel') {
                        return of(true);
                    }

                    return of(false);
                }),
            );
        } else {
            return of(true);
        }
    }

    setShouldShowConfirm(): boolean {
        return (
            this.pendingManageAssetResult !== undefined ||
            this.hasAssetChanged()
        );
    }

    onAssetAvailable() {
        this.originalAsset = this.cloneAsset(this.asset);
    }

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

    private async saveAssetChanges(): Promise<void> {
        this.cleanupEmpty();
        if (this.isNewAsset) {
            await firstValueFrom(this.assetService.createAsset(this.asset));
            this.isNewAsset = false;
        } else {
            await firstValueFrom(this.assetService.updateAsset(this.asset));
        }
        await this.savePendingManageAssetChanges();
        this.originalAsset = this.cloneAsset(this.asset);
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

    private hasAssetChanged(): boolean {
        if (!this.originalAsset || !this.asset) {
            return false;
        }

        return (
            JSON.stringify(this.originalAsset) !==
            JSON.stringify(this.cloneAsset(this.asset))
        );
    }

    private cloneAsset(asset: SpAssetModel): SpAssetModel {
        return JSON.parse(JSON.stringify(asset));
    }
}
