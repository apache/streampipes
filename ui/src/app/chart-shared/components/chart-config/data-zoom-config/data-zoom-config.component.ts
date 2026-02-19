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
    Output,
    SimpleChanges,
} from '@angular/core';
import { TimeSeriesAppearanceConfig } from '../../../models/dataview-dashboard.model';
import { SplitSectionComponent } from '@streampipes/shared-ui';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatCheckbox } from '@angular/material/checkbox';
import { FormsModule } from '@angular/forms';
import { MatFormField } from '@angular/material/form-field';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-data-zoom-config',
    templateUrl: './data-zoom-config.component.html',
    imports: [
        SplitSectionComponent,
        LayoutDirective,
        MatCheckbox,
        FormsModule,
        FlexDirective,
        LayoutAlignDirective,
        MatFormField,
        MatSelect,
        MatOption,
        TranslatePipe,
    ],
})
export class SpDataZoomConfigComponent implements OnChanges {
    @Input()
    appearanceConfig: TimeSeriesAppearanceConfig;

    @Output()
    configChangedEmitter: EventEmitter<void> = new EventEmitter<void>();

    ngOnChanges(changes: SimpleChanges) {
        if (changes.appearanceConfig) {
            this.appearanceConfig.dataZoom ??= {
                show: true,
                type: 'inside',
            };
        }
    }
}
