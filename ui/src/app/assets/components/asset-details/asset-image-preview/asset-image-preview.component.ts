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
    ElementRef,
    Input,
    OnChanges,
    OnDestroy,
    ViewChild,
    inject,
    signal,
} from '@angular/core';
import {
    AssetManagementService,
    SpAsset,
} from '@streampipes/platform-services';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { Subscription } from 'rxjs';

/**
 * Whether an asset shows an image: a pending selection (a file, or `null` for a pending
 * removal) takes precedence over the image stored on the asset.
 */
export function assetHasImage(
    asset: SpAsset | undefined,
    pendingImage?: File | null,
): boolean {
    return pendingImage !== undefined
        ? pendingImage !== null
        : !!asset?.imageId;
}

/**
 * Thumbnail of an asset image with a full-screen viewer. Shows the pending file when one is
 * given, otherwise loads the stored image of the asset. Content projected into the component
 * is placed on top of the thumbnail (e.g. a remove button).
 */
@Component({
    selector: 'sp-asset-image-preview',
    templateUrl: './asset-image-preview.component.html',
    styleUrls: ['./asset-image-preview.component.scss'],
    imports: [MatIcon, TranslatePipe],
})
export class AssetImagePreviewComponent implements OnChanges, OnDestroy {
    private assetService = inject(AssetManagementService);

    @Input()
    asset: SpAsset;

    @Input()
    assetModelId: string;

    @Input()
    pendingImage: File | null | undefined;

    /** Show a placeholder instead of nothing when the asset has no image. */
    @Input()
    showPlaceholder = false;

    @ViewChild('imageViewer')
    private imageViewer?: ElementRef<HTMLElement>;

    imageUrl = signal<string | undefined>(undefined);
    loadFailed = signal(false);

    private loadSubscription?: Subscription;

    get hasImage(): boolean {
        return assetHasImage(this.asset, this.pendingImage);
    }

    ngOnChanges(): void {
        this.reset();

        if (!this.hasImage) {
            return;
        }

        if (this.pendingImage) {
            this.imageUrl.set(URL.createObjectURL(this.pendingImage));
            return;
        }

        this.loadSubscription = this.assetService
            .getAssetImage(
                this.assetModelId,
                this.asset.imageId,
                this.asset.assetId,
            )
            .subscribe({
                next: blob => this.imageUrl.set(URL.createObjectURL(blob)),
                error: () => this.loadFailed.set(true),
            });
    }

    ngOnDestroy(): void {
        this.reset();
    }

    openViewer(): void {
        this.imageViewer?.nativeElement.showPopover();
    }

    closeViewer(): void {
        const viewer = this.imageViewer?.nativeElement;
        if (viewer?.matches(':popover-open')) {
            viewer.hidePopover();
        }
    }

    private reset(): void {
        this.loadSubscription?.unsubscribe();
        this.loadSubscription = undefined;
        this.closeViewer();
        const url = this.imageUrl();
        if (url) {
            URL.revokeObjectURL(url);
        }
        this.imageUrl.set(undefined);
        this.loadFailed.set(false);
    }
}
