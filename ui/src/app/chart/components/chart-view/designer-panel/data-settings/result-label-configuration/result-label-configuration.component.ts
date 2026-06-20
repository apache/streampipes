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

import { Component, Input, inject } from '@angular/core';
import {
    DataExplorerField,
    SourceConfig,
} from '@streampipes/platform-services';
import { ChartFieldProviderService } from '../../../../../../chart-shared/services/chart-field-provider.service';
import { ResultLabelService } from '../../../../../../chart-shared/services/result-label.service';
import { ChartConfigurationService } from '../../../../../../chart-shared/services/chart-configuration.service';
import { SplitSectionComponent } from '@streampipes/shared-ui';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-result-label-configuration',
    templateUrl: './result-label-configuration.component.html',
    styleUrls: ['./result-label-configuration.component.scss'],
    imports: [
        SplitSectionComponent,
        LayoutDirective,
        LayoutAlignDirective,
        LayoutGapDirective,
        FlexDirective,
        MatFormField,
        MatInput,
        FormsModule,
        TranslatePipe,
    ],
})
export class ResultLabelConfigurationComponent {
    private fieldProvider = inject(ChartFieldProviderService);
    private resultLabelService = inject(ResultLabelService);
    private widgetConfigService = inject(ChartConfigurationService);

    @Input() sourceConfig: SourceConfig;
    @Input() sourceIndex = 0;

    get selectedResultFields(): DataExplorerField[] {
        if (!this.sourceConfig?.measure) {
            return [];
        }

        return this.fieldProvider
            .generateFieldLists([this.sourceConfig])
            .allFields.sort((a, b) => a.fullDbName.localeCompare(b.fullDbName));
    }

    getLabel(field: DataExplorerField): string {
        return this.resultLabelService.resolveLabel(
            this.sourceConfig.queryConfig,
            { ...field, sourceIndex: this.sourceIndex },
        );
    }

    updateLabel(field: DataExplorerField, label: string): void {
        this.resultLabelService.setOverride(
            this.sourceConfig.queryConfig,
            { ...field, sourceIndex: this.sourceIndex },
            label,
            field.fullDbName,
        );

        this.widgetConfigService.notify({
            refreshData: false,
            refreshView: true,
        });
    }
}
