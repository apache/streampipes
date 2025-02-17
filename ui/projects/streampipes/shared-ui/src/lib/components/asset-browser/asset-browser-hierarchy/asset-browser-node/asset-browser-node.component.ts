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
    EventEmitter,
    Input,
    OnChanges,
    OnInit,
    Output,
    SimpleChanges,
} from '@angular/core';
import { SpAsset } from '@streampipes/platform-services';
import { SpAssetBrowserService } from '../../asset-browser.service';
import { AssetBrowserData } from '../../asset-browser.model';

@Component({
    selector: 'sp-asset-browser-node',
    templateUrl: 'asset-browser-node.component.html',
    styleUrls: ['./asset-browser-node.component.scss'],
})
export class AssetBrowserNodeComponent implements OnInit, OnChanges {
    @Input()
    assetBrowserData: AssetBrowserData;

    @Input()
    assetSelectionMode = false;

    @Input()
    node: SpAsset;

    @Input()
    selectedAsset: SpAsset;

    @Input()
    filteredAssetLinkType: string;

    @Input()
    resourceCount = 0;

    @Output()
    selectedNodeEmitter: EventEmitter<SpAsset> = new EventEmitter<SpAsset>();

    infoOpen = false;

    nodeResourceCount = 0;
    hasContextInfo = false;

    constructor(private assetBrowserService: SpAssetBrowserService) {}

    ngOnInit() {
        this.hasContextInfo =
            this.node.labelIds?.length > 0 ||
            this.node.assetSite?.siteId !== undefined ||
            this.node.assetType?.isa95AssetType !== undefined;
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.filteredAssetLinkType || changes.resourceCount) {
            this.reloadCounts();
        }
    }

    reloadCounts(): void {
        if (this.node.assetId !== '_root') {
            const elementIds = new Set<string>();
            this.assetBrowserService.collectElementIds(
                this.node,
                this.filteredAssetLinkType,
                elementIds,
            );
            this.nodeResourceCount = elementIds.size;
        } else {
            this.nodeResourceCount = this.resourceCount;
        }
    }

    emitSelectedNode(node: SpAsset): void {
        if (this.nodeResourceCount > 0 || this.assetSelectionMode) {
            this.selectedNodeEmitter.emit(node);
        }
    }
}
