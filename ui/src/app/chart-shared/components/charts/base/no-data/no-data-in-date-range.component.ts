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
    ChangeDetectionStrategy,
    inject,
} from '@angular/core';
import { TimeSettings } from '@streampipes/platform-services';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { TranslatePipe } from '@ngx-translate/core';
import { ChartTimeFormatService } from '../../../../services/formatting';

@Component({
    selector: 'sp-no-data-in-date-range',
    templateUrl: './no-data-in-date-range.component.html',
    styleUrls: ['./no-data-in-date-range.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        LayoutDirective,
        LayoutAlignDirective,
        FlexDirective,
        LayoutGapDirective,
        TranslatePipe,
    ],
})
export class NoDataInDateRangeComponent {
    private timeFormatService = inject(ChartTimeFormatService);

    @Input()
    public viewDateRange: TimeSettings;

    get rangeLabel(): string {
        return this.timeFormatService.formatRange(
            this.viewDateRange?.startTime,
            this.viewDateRange?.endTime,
        );
    }
}
