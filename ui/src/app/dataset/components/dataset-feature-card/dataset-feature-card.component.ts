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
import { FlexFillDirective } from '@ngbracket/ngx-layout';
import {
    DateFormatService,
    FeatureCardHeaderComponent,
    FeatureCardMetaSectionComponent,
    PropertyScopeBadgeComponent,
    SpLabelComponent,
} from '@streampipes/shared-ui';
import {
    AssetConstants,
    AssetLinkType,
    DataLakeMeasure,
    DatalakeRestService,
    EventPropertyUnion,
    GenericStorageService,
    SpQueryResult,
} from '@streampipes/platform-services';
import { forkJoin } from 'rxjs';
import { TranslatePipe } from '@ngx-translate/core';
import { MatIcon } from '@angular/material/icon';
import {
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';

@Component({
    selector: 'sp-dataset-feature-card',
    templateUrl: './dataset-feature-card.component.html',
    styleUrls: ['./dataset-feature-card.component.scss'],
    imports: [
        FlexFillDirective,
        LayoutDirective,
        TranslatePipe,
        LayoutAlignDirective,
        LayoutGapDirective,
        MatIcon,
        FeatureCardHeaderComponent,
        FeatureCardMetaSectionComponent,
        PropertyScopeBadgeComponent,
        SpLabelComponent,
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
    lastEventTs: number | undefined;
    previewRows: PreviewRow[] = [];

    private datalakeRestService = inject(DatalakeRestService);
    private genericStorageService = inject(GenericStorageService);
    private dateFormatService = inject(DateFormatService);

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
                    const previewRow = res.allDataSeries?.[0]?.rows?.[0] ?? [];
                    this.lastEventTs = Number(previewRow[0]);
                    this.previewRows = res.headers.map((header, index) =>
                        this.toPreviewRow(header, previewRow[index]),
                    );
                } else {
                    this.previewRows = [];
                    this.lastEventTs = undefined;
                }
            });
    }

    formatDate(timestamp?: number): string {
        return this.dateFormatService.formatDate(timestamp);
    }

    formatPreviewValue(header: string, value: unknown): string {
        if (value === null || value === undefined || value === '') {
            return '–';
        }

        if (this.isTimestampField(header)) {
            return this.dateFormatService.formatDate(Number(value));
        }

        if (typeof value === 'object') {
            try {
                return JSON.stringify(value);
            } catch {
                return String(value);
            }
        }

        return String(value);
    }

    private isTimestampField(header: string): boolean {
        return 'time' === header;
    }

    private toPreviewRow(header: string, value: unknown): PreviewRow {
        const eventProperty = this.findEventProperty(header);

        return {
            header,
            propertyScope: eventProperty?.propertyScope,
            value: this.formatPreviewValue(header, value),
        };
    }

    private findEventProperty(header: string): EventPropertyUnion | undefined {
        return this.dataset.eventSchema.eventProperties.find(
            property =>
                property.runtimeName.toLowerCase() === header.toLowerCase(),
        );
    }

    navigateToChartView(): void {}
}

interface PreviewRow {
    header: string;
    propertyScope?: string;
    value: string;
}
