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
    Input,
    OnChanges,
    OnInit,
    SimpleChanges,
} from '@angular/core';
import {
    LabelsService,
    SpAsset,
    SpLabel,
} from '@streampipes/platform-services';

@Component({
    selector: 'sp-view-asset-labels',
    templateUrl: './view-asset-labels.component.html',
})
export class ViewAssetLabelsComponent implements OnInit, OnChanges {
    @Input()
    asset: SpAsset;

    @Input()
    size: 'small' | 'medium' | 'large' = 'medium';

    allLabels: SpLabel[] = [];
    assignedLabels: SpLabel[] = [];

    constructor(private labelsService: LabelsService) {}

    ngOnInit() {
        this.labelsService.getAllLabels().subscribe(res => {
            this.allLabels = res;
            this.filterLabels();
        });
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes['asset']) {
            this.filterLabels();
        }
    }

    filterLabels(): void {
        this.assignedLabels = this.allLabels.filter(
            l => this.asset.labelIds.indexOf(l._id) > -1,
        );
    }
}
