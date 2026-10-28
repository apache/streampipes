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
import { FileMetadata, ImagesService } from '@streampipes/platform-services';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Component({
    selector: 'sp-view-asset-images',
    templateUrl: './view-asset-images.component.html',
    styleUrls: ['./view-asset-images.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [MatIcon, TranslatePipe],
})
export class ViewAssetImagesComponent implements OnChanges, OnDestroy {
    private imagesService = inject(ImagesService);

    @Input()
    imageFiles: FileMetadata[] = [];

    imageUrl: string | undefined;
    viewerOpen = false;

    private loadGeneration = 0;

    ngOnChanges(): void {
        this.loadImage();
    }

    ngOnDestroy(): void {
        this.loadGeneration++;
        this.clearImageUrl();
    }

    private loadImage(): void {
        const generation = ++this.loadGeneration;

        this.viewerOpen = false;
        this.clearImageUrl();

        const imageFile = this.imageFiles?.[0];
        if (!imageFile) {
            return;
        }

        this.imagesService
            .getImage(imageFile.fileId)
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

    private clearImageUrl(): void {
        if (this.imageUrl) URL.revokeObjectURL(this.imageUrl);
        this.imageUrl = undefined;
    }
}
