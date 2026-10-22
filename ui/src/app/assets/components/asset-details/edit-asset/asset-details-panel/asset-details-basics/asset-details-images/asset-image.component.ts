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
    EventEmitter,
    Component,
    ElementRef,
    Input,
    OnChanges,
    OnDestroy,
    Output,
    ViewChild,
    inject,
} from '@angular/core';
import {
    FileMetadata,
    ImagesService,
    SpAsset,
} from '@streampipes/platform-services';
import { FormFieldComponent } from '@streampipes/shared-ui';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import {
    DialogService,
    PanelType,
    ConfirmDialogAction,
    ConfirmDialogComponent,
} from '@streampipes/shared-ui';
import { AssetImageUploadDialogComponent } from './asset-image-upload/asset-image-upload-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

export interface AssetImageChange {
    asset: SpAsset;
    file: FileMetadata;
}

@Component({
    selector: 'sp-asset-image',
    templateUrl: './asset-image.component.html',
    styleUrl: './asset-image.component.scss',
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [FormFieldComponent, MatButton, MatIcon, TranslatePipe],
})
export class AssetImageComponent implements OnChanges, OnDestroy {
    private imagesService = inject(ImagesService);
    private dialogService = inject(DialogService);
    private translateService = inject(TranslateService);
    private dialog = inject(MatDialog);

    @Input()
    asset: SpAsset;

    @Input()
    editMode: boolean;

    @Output()
    imageUploaded = new EventEmitter<AssetImageChange>();

    @Output()
    imageLinkRemoval = new EventEmitter<AssetImageChange>();

    @Output()
    imageFileDeletionRequested = new EventEmitter<AssetImageChange>();

    imageFile: FileMetadata | undefined;
    imageUrl: string | undefined;

    @ViewChild('imageViewer')
    private imageViewer?: ElementRef<HTMLElement>;

    private loadGeneration = 0;

    ngOnChanges(): void {
        this.refreshImage();
    }

    ngOnDestroy(): void {
        this.loadGeneration++;
        this.closeImageViewer();
        this.clearImageUrl();
    }

    refreshImage(): void {
        const generation = ++this.loadGeneration;
        this.closeImageViewer();
        this.imageFile = this.asset.assetImages?.[0];
        this.clearImageUrl();

        if (!this.imageFile) {
            return;
        }

        this.imagesService
            .getImage(this.imageFile.fileId)
            .pipe(
                map(blob => URL.createObjectURL(blob)),
                catchError(() => of(undefined)),
            )
            .subscribe(url => {
                if (generation !== this.loadGeneration) {
                    if (url) URL.revokeObjectURL(url);
                    return;
                }
                this.imageUrl = url;
            });
    }

    openImageUploadDialog(): void {
        const asset = this.asset;
        if (asset.assetImages?.length) {
            this.dialog
                .open(ConfirmDialogComponent, {
                    width: '500px',
                    data: {
                        title: this.translateService.instant(
                            'Replace asset image?',
                        ),
                        subtitle: this.translateService.instant(
                            'The new image will replace the current image. The old file will be deleted when you save.',
                        ),
                        cancelTitle: this.translateService.instant('Cancel'),
                        confirmTitle:
                            this.translateService.instant('Replace image'),
                    },
                })
                .afterClosed()
                .subscribe((action: ConfirmDialogAction | undefined) => {
                    if (action === 'confirm') this.uploadImageForAsset(asset);
                });
        } else {
            this.uploadImageForAsset(asset);
        }
    }

    private uploadImageForAsset(asset: SpAsset): void {
        const dialogRef = this.dialogService.open(
            AssetImageUploadDialogComponent,
            {
                panelType: PanelType.SLIDE_IN_PANEL,
                title: this.translateService.instant('Upload image'),
                width: '40vw',
            },
        );

        dialogRef.afterClosed().subscribe((uploadedFile: FileMetadata) => {
            if (!uploadedFile) {
                return;
            }

            this.imageUploaded.emit({ asset, file: uploadedFile });
            this.refreshImage();
        });
    }

    private clearImageUrl(): void {
        if (this.imageUrl) URL.revokeObjectURL(this.imageUrl);
        this.imageUrl = undefined;
    }

    private closeImageViewer(): void {
        const viewer = this.imageViewer?.nativeElement;
        if (viewer?.matches(':popover-open')) viewer.hidePopover();
    }

    openImageRemovalDialog(event: MouseEvent, file: FileMetadata): void {
        event.stopPropagation();
        const asset = this.asset;

        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            data: {
                title: this.translateService.instant('Remove image?'),
                subtitle: this.translateService.instant(
                    'Remove this image from the asset, or permanently delete the image file.',
                ),
                neutralTitle:
                    this.translateService.instant('Remove from asset'),
                cancelTitle: this.translateService.instant('Cancel'),
                confirmTitle: this.translateService.instant('Delete file'),
            },
        });

        dialogRef
            .afterClosed()
            .subscribe((action: ConfirmDialogAction | undefined) => {
                if (action === 'neutral') {
                    this.imageLinkRemoval.emit({ asset, file });
                    this.refreshImage();
                } else if (action === 'confirm') {
                    this.imageFileDeletionRequested.emit({ asset, file });
                    this.refreshImage();
                }
            });
    }
}
