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
    ChangeDetectionStrategy,
    Component,
    OnInit,
    inject,
} from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatTableDataSource,
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import {
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { FileMetadata, ImagesService } from '@streampipes/platform-services';
import {
    ConfirmDialogComponent,
    DialogService,
    PanelType,
    SpAlertBannerComponent,
    SpBreadcrumbService,
    SpLabelComponent,
    SplitSectionComponent,
    SpTableComponent,
    SpTableNameSearchConfig,
} from '@streampipes/shared-ui';
import { saveAs } from 'file-saver';
import { AssetImageUploadDialogComponent } from '../../assets/components/asset-details/edit-asset/asset-details-panel/asset-details-basics/asset-details-images/asset-image-upload/asset-image-upload-dialog.component';
import { SpConfigurationRoutes } from '../configuration.breadcrumb';
import { SpConfigurationTabsService } from '../configuration-tabs.service';

@Component({
    selector: 'sp-images',
    templateUrl: './images.component.html',
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        DatePipe,
        LayoutDirective,
        LayoutAlignDirective,
        MatButton,
        MatIconButton,
        MatIcon,
        MatTooltip,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        MatCellDef,
        MatCell,
        SpAlertBannerComponent,
        SpLabelComponent,
        SplitSectionComponent,
        SpTableComponent,
        TranslatePipe,
    ],
})
export class ImagesComponent implements OnInit {
    private imagesService = inject(ImagesService);
    private dialog = inject(MatDialog);
    private dialogService = inject(DialogService);
    private translateService = inject(TranslateService);
    private breadcrumbService = inject(SpBreadcrumbService);
    private tabService = inject(SpConfigurationTabsService);

    readonly displayedColumns = ['filename', 'filetype', 'uploaded', 'action'];
    readonly dataSource = new MatTableDataSource<FileMetadata>();
    readonly nameSearchConfig: SpTableNameSearchConfig<FileMetadata> = {
        enabled: true,
        searchKey: ['filename'],
    };
    errorMessage = '';

    ngOnInit(): void {
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: this.tabService.getTabTitle('images') },
        ]);
        this.refreshImages();
    }

    refreshImages(): void {
        this.errorMessage = '';
        this.imagesService.getImageMetadata().subscribe({
            next: images => (this.dataSource.data = images),
            error: () => {
                this.errorMessage = this.translateService.instant(
                    'Could not load images',
                );
            },
        });
    }

    openImageUploadDialog(): void {
        this.dialogService
            .open(AssetImageUploadDialogComponent, {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: this.translateService.instant('Upload image'),
                width: '40vw',
            })
            .afterClosed()
            .subscribe(image => {
                if (image) this.refreshImages();
            });
    }

    downloadImage(image: FileMetadata): void {
        this.errorMessage = '';
        this.imagesService.getImage(image.fileId).subscribe({
            next: content => saveAs(content, image.filename),
            error: () => {
                this.errorMessage = this.translateService.instant(
                    'Could not download image',
                );
            },
        });
    }

    deleteImage(image: FileMetadata): void {
        this.errorMessage = '';
        this.imagesService.isImageInUse(image.fileId).subscribe({
            next: inUse => {
                if (inUse) {
                    this.showImageInUseWarning();
                } else {
                    this.confirmImageDeletion(image);
                }
            },
            error: () => this.showDeletionError(),
        });
    }

    private confirmImageDeletion(image: FileMetadata): void {
        this.dialog
            .open(ConfirmDialogComponent, {
                width: '500px',
                data: {
                    title: this.translateService.instant(
                        'Do you really want to delete this image?',
                    ),
                    subtitle: this.translateService.instant(
                        'This cannot be undone.',
                    ),
                    cancelTitle: this.translateService.instant('No'),
                    confirmTitle: this.translateService.instant('Yes'),
                },
            })
            .afterClosed()
            .subscribe(result => {
                if (result !== 'confirm') return;
                this.imagesService.deleteImage(image.fileId).subscribe({
                    next: () => this.refreshImages(),
                    error: error => {
                        if (
                            error instanceof HttpErrorResponse &&
                            error.status === 409
                        ) {
                            this.showImageInUseWarning();
                        } else {
                            this.showDeletionError();
                        }
                    },
                });
            });
    }

    private showImageInUseWarning(): void {
        this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            data: {
                title: this.translateService.instant('Image is still in use'),
                subtitle: this.translateService.instant(
                    'To delete this image, please remove the image from all assets.',
                ),
                confirmTitle: this.translateService.instant('Ok'),
            },
        });
    }

    private showDeletionError(): void {
        this.errorMessage = this.translateService.instant(
            'Could not delete image',
        );
    }
}
