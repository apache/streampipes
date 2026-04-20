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

import { Component, Input, OnInit, inject } from '@angular/core';
import { SourceConfig } from '@streampipes/platform-services';
import { ChartConfigurationService } from '../../../../../../chart-shared/services/chart-configuration.service';
import { SplitSectionComponent } from '@streampipes/shared-ui';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-order-selection-panel',
    templateUrl: './order-selection-panel.component.html',
    styleUrls: ['./order-selection-panel.component.scss'],
    imports: [
        SplitSectionComponent,
        MatRadioGroup,
        FormsModule,
        MatRadioButton,
        TranslatePipe,
    ],
})
export class OrderSelectionPanelComponent implements OnInit {
    private widgetConfigService = inject(ChartConfigurationService);

    @Input() sourceConfig: SourceConfig;

    ngOnInit(): void {
        this.sourceConfig.queryConfig.order ??= 'DESC';
    }

    triggerConfigurationUpdate() {
        this.widgetConfigService.notify({
            refreshData: true,
            refreshView: true,
        });
    }
}
