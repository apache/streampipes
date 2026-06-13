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
    OnInit,
    Output,
    inject,
} from '@angular/core';
import { ChartSummaryDto } from '@streampipes/platform-services';
import { ChartRegistry } from '../../../../../../chart-shared/registry/chart-registry.service';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { FeatureCardService } from '@streampipes/shared-ui';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-chart-preview',
    templateUrl: './chart-preview.component.html',
    styleUrls: ['./chart-preview.component.scss'],
    imports: [
        LayoutDirective,
        FlexDirective,
        LayoutGapDirective,
        LayoutAlignDirective,
        MatIcon,
        MatIconButton,
        MatTooltip,
        TranslatePipe,
    ],
})
export class ChartPreviewComponent implements OnInit {
    private widgetRegistryService = inject(ChartRegistry);
    private featureCardService = inject(FeatureCardService);

    @Input()
    chart: ChartSummaryDto;

    widgetTypeLabel = '';
    widgetTypeIcon = 'insert_chart';

    @Output()
    addChartEmitter: EventEmitter<string> = new EventEmitter<string>();

    ngOnInit(): void {
        const template = this.widgetRegistryService.getChartTemplate(
            this.chart.widgetType,
        );
        this.widgetTypeLabel = template?.label ?? this.chart.widgetType;
        this.widgetTypeIcon = template?.icon ?? 'insert_chart';
    }

    addChart(): void {
        this.addChartEmitter.emit(this.chart.elementId);
    }

    openPreview(event: MouseEvent): void {
        event.stopPropagation();
        this.featureCardService.openFeatureCard('chart', this.chart.elementId);
    }
}
