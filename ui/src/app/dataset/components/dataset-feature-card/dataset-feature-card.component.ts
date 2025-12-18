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

import { Component, inject, Input, OnInit } from '@angular/core';
import {
    DefaultFlexDirective,
    DefaultLayoutAlignDirective,
    DefaultLayoutDirective,
    DefaultLayoutGapDirective,
    FlexFillDirective,
} from '@ngbracket/ngx-layout';
import { SharedUiModule } from '@streampipes/shared-ui';
import { DashboardSharedModule } from '../../../dashboard-shared/dashboard-shared.module';
import {
    AssetConstants,
    AssetLinkType,
    DataLakeMeasure,
    DatalakeRestService,
    GenericStorageService,
    SpQueryResult,
} from '@streampipes/platform-services';
import { forkJoin } from 'rxjs';
import { TranslatePipe } from '@ngx-translate/core';
import { MatIcon } from '@angular/material/icon';
import { DatePipe } from '@angular/common';

@Component({
    selector: 'sp-dataset-feature-card',
    templateUrl: './dataset-feature-card.component.html',
    styleUrls: ['./dataset-feature-card.component.scss'],
    imports: [
        SharedUiModule,
        FlexFillDirective,
        DashboardSharedModule,
        DefaultFlexDirective,
        DefaultLayoutDirective,
        TranslatePipe,
        DefaultLayoutAlignDirective,
        DefaultLayoutGapDirective,
        MatIcon,
        DatePipe,
    ],
})
export class DatasetFeatureCardComponent implements OnInit {
    @Input()
    resourceId: string;

    @Input()
    onClose?: () => void;

    dataset: DataLakeMeasure;
    assetLinkType: AssetLinkType;
    dataPreview: SpQueryResult;
    lastEventTs: number;

    private datalakeRestService = inject(DatalakeRestService);
    private genericStorageService = inject(GenericStorageService);

    ngOnInit() {
        forkJoin([
            this.datalakeRestService.getMeasurement(this.resourceId),
            this.genericStorageService.getAllDocuments(
                AssetConstants.ASSET_LINK_TYPES_DOC_NAME,
            ),
        ]).subscribe(res => {
            this.dataset = res[0];
            this.assetLinkType = res[1].find(a => a.linkType === 'measurement');
            this.loadSampleData();
        });
    }

    loadSampleData(): void {
        this.datalakeRestService
            .getData(this.dataset.measureName, {
                endDate: new Date().getTime(),
                startDate: 0,
                limit: 1,
                order: 'DESC',
                missingValueBehaviour: 'empty',
                columns: this.dataset.eventSchema.eventProperties
                    .map(ep => ep.runtimeName)
                    .toString(),
            })
            .subscribe(res => {
                this.dataPreview = res;
                if (res.total > 0) {
                    this.lastEventTs = res.allDataSeries[0].rows[0][0];
                }
            });
    }

    navigateToChartView(): void {}
}
