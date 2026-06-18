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

import { Component, OnInit, inject } from '@angular/core';
import {
    PipelineElementSchemaService,
    PropertyScopeBadgeComponent,
    SpBasicHeaderTitleComponent,
    SpBasicNavTabsComponent,
    SpElementIdComponent,
} from '@streampipes/shared-ui';
import { SpAbstractDatasetDetailsDirective } from '../abstract-dataset-details.directive';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatTable,
} from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { SpConfigurationRoutes } from '../../../../configuration/configuration.breadcrumb';

interface SchemaRow {
    runtimeName: string;
    label: string;
    dataType: string;
    propertyScope: string;
    description: string;
}

@Component({
    selector: 'sp-dataset-details-schema',
    templateUrl: './dataset-details-schema.component.html',
    styleUrls: ['./dataset-details-schema.component.scss'],
    imports: [
        SpBasicNavTabsComponent,
        SpBasicHeaderTitleComponent,
        SpElementIdComponent,
        PropertyScopeBadgeComponent,
        LayoutDirective,
        LayoutAlignDirective,
        FlexDirective,
        MatTable,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        MatCellDef,
        MatCell,
        MatHeaderRowDef,
        MatHeaderRow,
        MatRowDef,
        MatRow,
        TranslatePipe,
    ],
})
export class DatasetDetailsSchemaComponent
    extends SpAbstractDatasetDetailsDirective
    implements OnInit
{
    private pipelineElementSchemaService = inject(PipelineElementSchemaService);

    schemaRows: SchemaRow[] = [];
    schemaColumns = ['runtimeName', 'label', 'dataType', 'description'];

    ngOnInit(): void {
        super.onInit();
    }

    onDatasetLoaded(): void {
        this.schemaRows = this.makeSchemaRows();
        this.breadcrumbService.updateBreadcrumb([
            SpConfigurationRoutes.BASE,
            { label: 'Datasets', link: ['datasets'] },
            { label: this.dataset.measureName },
            { label: 'Event schema' },
        ]);
    }

    private makeSchemaRows(): SchemaRow[] {
        const eventProperties = this.dataset.eventSchema?.eventProperties ?? [];
        const schemaRows = eventProperties.map(property => ({
            runtimeName: property.runtimeName || 'n/a',
            label: property.label || 'n/a',
            dataType:
                this.pipelineElementSchemaService.getFriendlyRuntimeType(
                    property,
                ),
            propertyScope: property.propertyScope,
            description: property.description || 'n/a',
        }));

        return [this.makeTimestampRow(), ...schemaRows];
    }

    private makeTimestampRow(): SchemaRow {
        return {
            runtimeName: this.dataset.timestampField || 'time',
            label: 'time',
            dataType: 'Timestamp',
            propertyScope: 'HEADER_PROPERTY',
            description: 'Dataset internal timestamp field',
        };
    }
}
