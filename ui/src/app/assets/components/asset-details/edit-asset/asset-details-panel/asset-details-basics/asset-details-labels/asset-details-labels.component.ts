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
    inject,
} from '@angular/core';
import {
    LabelsService,
    SpAsset,
    SpLabel,
} from '@streampipes/platform-services';
import {
    FormFieldComponent,
    SpLabelComponent,
    SearchSelectComponent,
} from '@streampipes/shared-ui';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton, MatIconButton } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-asset-details-labels',
    templateUrl: './asset-details-labels.component.html',
    imports: [
        LayoutDirective,
        LayoutGapDirective,
        LayoutAlignDirective,
        FlexDirective,
        FormFieldComponent,
        MatButton,
        RouterLink,
        MatIconButton,
        MatIcon,
        SearchSelectComponent,
        SpLabelComponent,
        TranslatePipe,
    ],
})
export class AssetDetailsLabelsComponent implements OnInit, OnChanges {
    private labelsService = inject(LabelsService);

    @Input()
    asset: SpAsset;

    @Input()
    editMode: boolean;

    labels: SpLabel[] = [];
    allLabels: SpLabel[] = [];
    labelsAvailable = false;

    ngOnInit(): void {
        this.loadLabels();
    }

    loadLabels(): void {
        this.labelsService.getAllLabels().subscribe(labels => {
            this.allLabels = labels.sort((a, b) =>
                a.label.localeCompare(b.label),
            );
            this.refreshCurrentLabels();
            this.labelsAvailable = true;
        });
    }

    refreshCurrentLabels(): void {
        this.asset.labelIds =
            this.asset.labelIds?.filter(id =>
                this.allLabels.find(l => l._id === id),
            ) || [];
        this.labels =
            this.asset.labelIds?.map(id =>
                this.allLabels.find(l => l._id === id),
            ) || [];
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes['asset'] && this.labelsAvailable) {
            this.refreshCurrentLabels();
        }
    }

    onLabelsChange(labels: SpLabel | SpLabel[] | undefined): void {
        this.labels = Array.isArray(labels) ? labels : [];
        this.asset.labelIds = this.labels.map(label => label._id);
    }
}
