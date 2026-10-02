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
    inject,
    Input,
    OnChanges,
    OnDestroy,
} from '@angular/core';
import {
    FileMetadata,
    FilesService,
    SpAsset,
} from '@streampipes/platform-services';
import { MatIcon } from '@angular/material/icon';
import { forkJoin, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

@Component({
    selector: 'sp-view-asset-images',
    templateUrl: './view-asset-images.component.html',
    styleUrls: ['./view-asset-images.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [MatIcon],
})
export class ViewAssetImagesComponent implements OnChanges, OnDestroy {
    private filesService = inject(FilesService);

    @Input()
    asset: SpAsset;

    imageUrls: string[] = [];

    selectedImageIndex: number | null = null;

    private loadGeneration = 0;

    ngOnChanges(): void {
        this.loadImages();
    }

    ngOnDestroy(): void {
        this.loadGeneration++;
        this.clearImageUrls();
    }

    get additionalImageCount(): number {
        return Math.max(0, this.imageUrls.length - 1);
    }

    openViewer(index: number): void {
        if (this.imageUrls.length === 0) {
            return;
        }

        this.selectedImageIndex = index;
    }

    closeViewer(): void {
        this.selectedImageIndex = null;
    }

    previousImage(): void {
        if (this.selectedImageIndex === null || this.imageUrls.length === 0) {
            return;
        }

        this.selectedImageIndex =
            (this.selectedImageIndex - 1 + this.imageUrls.length) %
            this.imageUrls.length;
    }

    nextImage(): void {
        if (this.selectedImageIndex === null || this.imageUrls.length === 0) {
            return;
        }

        this.selectedImageIndex =
            (this.selectedImageIndex + 1) % this.imageUrls.length;
    }

    private loadImages(): void {
        const generation = ++this.loadGeneration;

        this.selectedImageIndex = null;
        this.clearImageUrls();

        const linkedFileIds = [
            ...new Set(
                (this.asset?.assetLinks ?? [])
                    .filter(
                        link => link.linkType === 'file' && !!link.resourceId,
                    )
                    .map(link => link.resourceId),
            ),
        ];

        if (linkedFileIds.length === 0) {
            return;
        }

        this.filesService
            .getFileMetadata(['jpg', 'jpeg'])
            .pipe(
                map(files => this.getLinkedImageFiles(files, linkedFileIds)),
                switchMap(files => {
                    if (files.length === 0) {
                        return of<(string | null)[]>([]);
                    }

                    return forkJoin(
                        files.map(file =>
                            this.filesService.getFile(file.filename).pipe(
                                map(blob => URL.createObjectURL(blob)),
                                catchError(() => of(null)),
                            ),
                        ),
                    );
                }),
            )
            .subscribe(urls => {
                const validUrls = urls.filter(
                    (url): url is string => url !== null,
                );

                if (generation !== this.loadGeneration) {
                    validUrls.forEach(url => URL.revokeObjectURL(url));
                    return;
                }

                this.imageUrls = validUrls;
            });
    }

    private getLinkedImageFiles(
        files: FileMetadata[],
        linkedFileIds: string[],
    ): FileMetadata[] {
        const filesById = new Map(files.map(file => [file.fileId, file]));

        return linkedFileIds
            .map(fileId => filesById.get(fileId))
            .filter((file): file is FileMetadata => file !== undefined);
    }

    private clearImageUrls(): void {
        this.imageUrls.forEach(url => URL.revokeObjectURL(url));

        this.imageUrls = [];
    }
}
