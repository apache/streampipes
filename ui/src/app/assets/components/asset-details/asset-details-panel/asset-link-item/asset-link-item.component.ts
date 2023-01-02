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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AssetLink, AssetLinkType } from '@streampipes/platform-services';
import { Router } from '@angular/router';

@Component({
    selector: 'sp-asset-link-item-component',
    templateUrl: './asset-link-item.component.html',
    styleUrls: ['./asset-link-item.component.scss'],
})
export class SpAssetLinkItemComponent implements OnInit {
    @Input()
    assetLink: AssetLink;

    @Input()
    assetLinkIndex: number;

    @Input()
    assetLinkTypes: AssetLinkType[];

    @Input()
    editMode: boolean;

    @Output()
    openEditAssetLinkEmitter: EventEmitter<{
        assetLink: AssetLink;
        index: number;
    }> = new EventEmitter<{ assetLink: AssetLink; index: number }>();

    @Output()
    deleteAssetLinkEmitter: EventEmitter<number> = new EventEmitter<number>();

    currentAssetLinkType: AssetLinkType;

    constructor(private router: Router) {}

    ngOnInit(): void {
        this.currentAssetLinkType = this.assetLinkTypes.find(
            t => t.linkType === this.assetLink.linkType,
        );
    }

    openLink(): void {
        this.router.navigate([
            ...this.currentAssetLinkType.navPaths,
            this.assetLink.resourceId,
        ]);
    }

    editLink(): void {
        this.openEditAssetLinkEmitter.emit({
            assetLink: this.assetLink,
            index: this.assetLinkIndex,
        });
    }

    deleteLink(): void {
        this.deleteAssetLinkEmitter.emit(this.assetLinkIndex);
    }
}
