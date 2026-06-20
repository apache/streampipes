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
    ChangeDetectionStrategy,
    Component,
    EventEmitter,
    Input,
    Output,
    inject,
} from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { FeatureCardService } from '@streampipes/shared-ui';
import { TranslatePipe } from '@ngx-translate/core';
import { ChartSelectionItem } from '../chart-selection.model';

@Component({
    selector: 'sp-chart-preview',
    templateUrl: './chart-preview.component.html',
    styleUrls: ['./chart-preview.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [MatIcon, MatIconButton, TranslatePipe],
})
export class ChartPreviewComponent {
    private featureCardService = inject(FeatureCardService);

    @Input()
    chartItem!: ChartSelectionItem;

    @Output()
    addChartEmitter: EventEmitter<string> = new EventEmitter<string>();

    addChart(): void {
        this.addChartEmitter.emit(this.chartItem.chart.elementId);
    }

    openPreview(event: MouseEvent): void {
        event.stopPropagation();
        this.featureCardService.openFeatureCard(
            'chart',
            this.chartItem.chart.elementId,
        );
    }
}
