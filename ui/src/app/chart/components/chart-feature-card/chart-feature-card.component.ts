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
    AssetConstants,
    AssetLinkType,
    ChartService,
    DataExplorerWidgetModel,
    GenericStorageService,
    TimeSettings,
} from '@streampipes/platform-services';
import { forkJoin } from 'rxjs';
import {
    DefaultFlexDirective,
    DefaultLayoutAlignDirective,
    DefaultLayoutDirective,
    DefaultLayoutGapDirective,
    FlexFillDirective,
} from '@ngbracket/ngx-layout';
import { SharedUiModule } from '@streampipes/shared-ui';
import { ChartSharedModule } from '../../../chart-shared/chart-shared.module';
import { ChartSharedService } from '../../../chart-shared/services/chart-shared.service';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { ChartRegistry } from '../../../chart-shared/registry/chart-registry.service';
import { Router } from '@angular/router';

@Component({
    selector: 'sp-chart-feature-card',
    templateUrl: './chart-feature-card.component.html',
    imports: [
        FlexFillDirective,
        DefaultLayoutDirective,
        SharedUiModule,
        ChartSharedModule,
        DefaultFlexDirective,
        DefaultLayoutAlignDirective,
        DefaultLayoutGapDirective,
        MatIcon,
        TranslatePipe,
    ],
    styleUrls: ['./chart-feature-card.component.scss'],
})
export class ChartFeatureCardComponent implements OnInit {
    @Input()
    resourceId: string;

    @Input() onClose?: () => void;

    chart: DataExplorerWidgetModel;
    assetLinkType: AssetLinkType;
    timeSettings: TimeSettings;

    private chartService = inject(ChartService);
    private genericStorageService = inject(GenericStorageService);
    private chartSharedService = inject(ChartSharedService);
    private chartRegistryService = inject(ChartRegistry);
    private router = inject(Router);

    chartType: string = 'Unknown';

    observableGenerator = this.chartSharedService.defaultObservableGenerator();

    ngOnInit() {
        forkJoin([
            this.chartService.getChart(this.resourceId),
            this.genericStorageService.getAllDocuments(
                AssetConstants.ASSET_LINK_TYPES_DOC_NAME,
            ),
        ]).subscribe(res => {
            this.chart = res[0];
            this.assetLinkType = res[1].find(a => a.linkType === 'chart');
            this.timeSettings = this.chartSharedService.makeChartTimeSettings(
                this.chart,
            );
            this.chartType = this.chartRegistryService.getChartTemplate(
                this.chart.widgetType,
            ).label;
        });
    }

    navigateToChart(): void {
        this.onClose();
        this.router.navigate(['chart', this.resourceId]);
    }
}
