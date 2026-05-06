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
    ChangeDetectorRef,
    Component,
    EventEmitter,
    inject,
    Input,
    OnInit,
    Output,
} from '@angular/core';
import {
    AssetLinkType,
    Isa95TypeService,
} from '@streampipes/platform-services';
import { Router } from '@angular/router';
import { SpLabelComponent } from '@streampipes/shared-ui';
import { AssetLinkChipComponent } from './asset-link-chip/asset-link-chip.component';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { AssetPopupEntry } from '../home-asset-map.types';

export type PopupAction = 'details' | 'pipelines' | 'dashboards';

@Component({
    selector: 'sp-asset-map-popup',
    templateUrl: './asset-map-popup.component.html',
    styleUrls: ['./asset-map-popup.component.scss'],
    imports: [
        SpLabelComponent,
        AssetLinkChipComponent,
        MatButton,
        MatIconButton,
        MatIcon,
        TranslatePipe,
    ],
})
export class AssetMapPopupComponent implements OnInit {
    @Input()
    entries: AssetPopupEntry[] = [];

    @Input()
    assetLinkTypes: Record<string, AssetLinkType> = {};

    @Output() actionClicked = new EventEmitter<PopupAction>();

    activeEntry!: AssetPopupEntry;
    isa95Type = '';

    private isa95TypeService = inject(Isa95TypeService);
    private router = inject(Router);
    private changeDetectorRef = inject(ChangeDetectorRef);

    ngOnInit() {
        if (this.entries.length === 0) {
            return;
        }

        this.activeEntry = this.entries[0];
        this.isa95Type = this.isa95TypeService.toLabel(
            this.activeEntry.asset.assetType.isa95AssetType,
        );
    }

    navigateToAsset(): void {
        this.router.navigate([
            'assets',
            'details',
            this.activeEntry.asset.elementId,
            'view',
        ]);
    }

    previousAsset(): void {
        const currentIndex = this.entries.findIndex(
            entry => entry.asset.elementId === this.activeEntry.asset.elementId,
        );

        if (currentIndex > 0) {
            this.setActiveEntry(this.entries[currentIndex - 1]);
        }
    }

    nextAsset(): void {
        const currentIndex = this.entries.findIndex(
            entry => entry.asset.elementId === this.activeEntry.asset.elementId,
        );

        if (currentIndex < this.entries.length - 1) {
            this.setActiveEntry(this.entries[currentIndex + 1]);
        }
    }

    hasPreviousAsset(): boolean {
        return (
            this.entries.findIndex(
                entry =>
                    entry.asset.elementId === this.activeEntry.asset.elementId,
            ) > 0
        );
    }

    hasNextAsset(): boolean {
        const currentIndex = this.entries.findIndex(
            entry => entry.asset.elementId === this.activeEntry.asset.elementId,
        );

        return currentIndex > -1 && currentIndex < this.entries.length - 1;
    }

    activeAssetIndex(): number {
        return this.entries.findIndex(
            entry => entry.asset.elementId === this.activeEntry.asset.elementId,
        );
    }

    private setActiveEntry(entry: AssetPopupEntry): void {
        this.activeEntry = entry;
        this.isa95Type = this.isa95TypeService.toLabel(
            entry.asset.assetType.isa95AssetType,
        );
        this.changeDetectorRef.detectChanges();
    }
}
