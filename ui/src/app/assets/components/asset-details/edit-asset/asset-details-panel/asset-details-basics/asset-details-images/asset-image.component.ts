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
    Input,
    OnInit,
    Output,
    inject,
} from '@angular/core';
import {
    FileMetadata,
    FilesService,
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
import { forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
    selector: 'sp-asset-image',
    templateUrl: './asset-image.component.html',
    styleUrl: './asset-image.component.scss',
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [FormFieldComponent, MatButton, MatIcon, TranslatePipe],
})
export class AssetImageComponent implements OnInit {
    private filesService = inject(FilesService);
    private dialogService = inject(DialogService);
    private translateService = inject(TranslateService);
    private dialog = inject(MatDialog);

    @Input()
    asset: SpAsset;

    @Input()
    editMode: boolean;

    @Output()
    imageUploaded: EventEmitter<FileMetadata> =
        new EventEmitter<FileMetadata>();

    @Output()
    imageLinkRemoval = new EventEmitter<string>();

    @Output()
    imageFileDeletionRequested = new EventEmitter<string>();

    imageFiles: FileMetadata[] = [];
    imageUrls: string[] = [];
    selectedImageIndex: number | null = null;

    ngOnInit(): void {
        this.refreshImages();
    }

    refreshImages(): void {
        this.filesService.getFileMetadata(['jpg', 'jpeg']).subscribe(files => {
            this.imageFiles = files.filter(file =>
                this.asset.assetLinks?.some(
                    link =>
                        link.linkType === 'file' &&
                        link.resourceId === file.fileId,
                ),
            );

            this.imageUrls.forEach(url => URL.revokeObjectURL(url));
            this.imageUrls = [];

            if (this.imageFiles.length === 0) {
                this.selectedImageIndex = null;
                return;
            }

            forkJoin(
                this.imageFiles.map(file =>
                    this.filesService
                        .getFile(file.filename)
                        .pipe(map(blob => URL.createObjectURL(blob))),
                ),
            ).subscribe(urls => {
                this.imageUrls = urls;

                if (
                    this.selectedImageIndex !== null &&
                    this.selectedImageIndex >= urls.length
                ) {
                    this.selectedImageIndex = null;
                }
            });
        });
    }

    openImageUploadDialog(): void {
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

            this.imageUploaded.emit(uploadedFile);
        });
    }

    previousImage(): void {
        if (this.selectedImageIndex === null) {
            return;
        }

        this.selectedImageIndex =
            (this.selectedImageIndex - 1 + this.imageUrls.length) %
            this.imageUrls.length;
    }

    nextImage(): void {
        if (this.selectedImageIndex === null) {
            return;
        }

        this.selectedImageIndex =
            (this.selectedImageIndex + 1) % this.imageUrls.length;
    }

    openImageRemovalDialog(event: MouseEvent, file: FileMetadata): void {
        event.stopPropagation();

        const dialogRef = this.dialog.open(ConfirmDialogComponent, {
            width: '500px',
            data: {
                title: this.translateService.instant('Remove image?'),
                subtitle: this.translateService.instant(
                    'Remove the link only, or permanently delete the image file.',
                ),
                neutralTitle: this.translateService.instant('Remove link only'),
                cancelTitle: this.translateService.instant('Cancel'),
                confirmTitle: this.translateService.instant('Delete file'),
            },
        });

        dialogRef
            .afterClosed()
            .subscribe((action: ConfirmDialogAction | undefined) => {
                if (action === 'neutral') {
                    this.imageLinkRemoval.emit(file.fileId);
                    return;
                }

                if (action === 'confirm') {
                    this.imageLinkRemoval.emit(file.fileId);
                    this.imageFileDeletionRequested.emit(file.fileId);
                }
            });
    }
}
