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
    ViewChild,
    inject,
} from '@angular/core';
import {
    DataExplorerDataConfig,
    DataExplorerWidgetModel,
    DataLakeMeasure,
    DatasetSummaryDto,
    DatalakeRestService,
    SourceConfig,
} from '@streampipes/platform-services';
import { Tuple2 } from '../../../../../core-model/base/Tuple2';
import { ActivatedRoute, Router } from '@angular/router';
import { ChartConfigurationService } from '../../../../../chart-shared/services/chart-configuration.service';
import { FieldSelectionPanelComponent } from './field-selection-panel/field-selection-panel.component';
import { GroupSelectionPanelComponent } from './group-selection-panel/group-selection-panel.component';
import { TableVisConfig } from '../../../../../chart-shared/components/charts/table/model/table-widget.model';
import { ChartFieldProviderService } from '../../../../../chart-shared/services/chart-field-provider.service';
import { FieldProvider } from '../../../../../chart-shared/models/dataview-dashboard.model';
import { ChartTypeService } from '../../../../../chart-shared/services/chart-type.service';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import {
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelHeader,
} from '@angular/material/expansion';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import {
    FormFieldComponent,
    SpAlertBannerComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import {
    MatFormField,
    MatPrefix,
    MatSuffix,
} from '@angular/material/form-field';
import { MatOption } from '@angular/material/core';
import { MatIcon } from '@angular/material/icon';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { FormsModule } from '@angular/forms';
import { NgClass } from '@angular/common';
import { ClassDirective } from '@ngbracket/ngx-layout/extended';
import { MatInput } from '@angular/material/input';
import { MatCheckbox } from '@angular/material/checkbox';
import { AggregateConfigurationComponent } from './aggregate-configuration/aggregate-configuration.component';
import { FillConfigurationComponent } from './fill-configuration/fill-configuration.component';
import { FilterSelectionPanelComponent } from './filter-selection-panel/filter-selection-panel.component';
import { OrderSelectionPanelComponent } from './order-selection-panel/order-selection-panel.component';
import { ResultLabelConfigurationComponent } from './result-label-configuration/result-label-configuration.component';
import { TranslatePipe } from '@ngx-translate/core';
import {
    MatAutocomplete,
    MatAutocompleteSelectedEvent,
    MatAutocompleteTrigger,
} from '@angular/material/autocomplete';

@Component({
    selector: 'sp-chart-data-settings',
    templateUrl: './chart-data-settings.component.html',
    styleUrls: ['./chart-data-settings.component.scss'],
    imports: [
        FlexDirective,
        LayoutDirective,
        MatAccordion,
        MatExpansionPanel,
        MatExpansionPanelHeader,
        LayoutAlignDirective,
        MatIconButton,
        MatTooltip,
        SplitSectionComponent,
        SpAlertBannerComponent,
        LayoutGapDirective,
        MatButton,
        MatFormField,
        MatPrefix,
        MatSuffix,
        MatOption,
        MatIcon,
        MatRadioGroup,
        FormsModule,
        NgClass,
        ClassDirective,
        MatRadioButton,
        FormFieldComponent,
        MatInput,
        MatCheckbox,
        MatAutocomplete,
        MatAutocompleteTrigger,
        AggregateConfigurationComponent,
        FillConfigurationComponent,
        FieldSelectionPanelComponent,
        FilterSelectionPanelComponent,
        GroupSelectionPanelComponent,
        OrderSelectionPanelComponent,
        ResultLabelConfigurationComponent,
        TranslatePipe,
    ],
})
export class ChartDataSettingsComponent implements OnInit {
    private datalakeRestService = inject(DatalakeRestService);
    private widgetConfigService = inject(ChartConfigurationService);
    private fieldProviderService = inject(ChartFieldProviderService);
    private widgetTypeService = inject(ChartTypeService);
    private router = inject(Router);
    private route = inject(ActivatedRoute);

    @Input() dataConfig: DataExplorerDataConfig;
    @Input() dataLakeMeasure: DataLakeMeasure;
    @Input() newWidgetMode: boolean;
    @Input() widgetId: string;
    @Input() currentlyConfiguredWidget: DataExplorerWidgetModel;

    @Output() createWidgetEmitter: EventEmitter<
        Tuple2<DataLakeMeasure, DataExplorerWidgetModel>
    > = new EventEmitter<Tuple2<DataLakeMeasure, DataExplorerWidgetModel>>();
    @Output() dataLakeMeasureChange: EventEmitter<DataLakeMeasure> =
        new EventEmitter<DataLakeMeasure>();
    @Output() configureVisualizationEmitter: EventEmitter<void> =
        new EventEmitter<void>();

    @ViewChild('fieldSelectionPanel')
    fieldSelectionPanel: FieldSelectionPanelComponent;

    @ViewChild('groupSelectionPanel')
    groupSelectionPanel: GroupSelectionPanelComponent;

    availableMeasurements: DatasetSummaryDto[] = [];
    filteredMeasurements: DatasetSummaryDto[] = [];
    measurementInputValue = '';

    step = 0;

    expandFieldsDataSource = true;
    expandFieldsQuery = true;

    get sourceConfig(): SourceConfig | undefined {
        return this.dataConfig?.sourceConfigs?.[0];
    }

    ngOnInit(): void {
        this.syncCurrentMeasure();
        this.loadPipelinesAndMeasurements();
    }

    loadPipelinesAndMeasurements() {
        this.datalakeRestService.getMeasurementSummary().subscribe(response => {
            this.availableMeasurements = response.resources.sort((a, b) =>
                a.measureName.localeCompare(b.measureName),
            );
            this.applyMeasurementSearch();

            if (!this.sourceConfig) {
                const defaultConfigs = this.findDefaultConfig();
                this.initializeSourceConfig(defaultConfigs.measureName);
                if (defaultConfigs.measureName !== undefined) {
                    this.loadMeasurement(
                        defaultConfigs.measureName,
                        true,
                        true,
                    );
                }
            } else if (
                !this.sourceConfig.measure &&
                this.sourceConfig.measureName
            ) {
                this.loadMeasurement(
                    this.sourceConfig.measureName,
                    false,
                    false,
                );
            }
        });
    }

    findDefaultConfig(): {
        measureName: string | undefined;
    } {
        const measureNameFromQueryParams =
            this.route.snapshot.queryParams.measureName;
        const matchingMeasurement = this.availableMeasurements.find(
            measurement =>
                measurement.measureName === measureNameFromQueryParams,
        );
        if (matchingMeasurement) {
            return {
                measureName: matchingMeasurement.measureName,
            };
        }

        if (this.availableMeasurements.length > 0) {
            return {
                measureName: this.availableMeasurements[0].measureName,
            };
        } else {
            return { measureName: undefined };
        }
    }

    updateMeasure(sourceConfig: SourceConfig, measureName: string) {
        sourceConfig.measureName = measureName;
        this.measurementInputValue = measureName;
        this.loadMeasurement(measureName, true, true);
    }

    onMeasurementSearchChange(value: string): void {
        this.measurementInputValue = value;
        this.applyMeasurementSearch();
    }

    clearMeasurementSearch(): void {
        this.measurementInputValue = '';
        this.applyMeasurementSearch();
    }

    hasActiveMeasurementSearch(): boolean {
        return this.measurementInputValue.trim().length > 0;
    }

    onMeasurementSelected(
        sourceConfig: SourceConfig,
        event: MatAutocompleteSelectedEvent,
    ): void {
        this.updateMeasure(sourceConfig, event.option.value);
    }

    private applyMeasurementSearch(): void {
        const query = this.measurementInputValue.trim().toLowerCase();
        if (!query) {
            this.filteredMeasurements = this.availableMeasurements;
            return;
        }

        this.filteredMeasurements = this.availableMeasurements.filter(
            measurement =>
                measurement.measureName.toLowerCase().includes(query) ||
                measurement.pipelines.some(pipeline =>
                    pipeline.toLowerCase().includes(query),
                ),
        );
    }

    private loadMeasurement(
        measureName: string,
        resetQueryConfig: boolean,
        refreshData: boolean,
    ): void {
        this.datalakeRestService
            .getMeasurementByName(measureName)
            .subscribe(measure =>
                this.applySelectedMeasurement(
                    measure,
                    resetQueryConfig,
                    refreshData,
                ),
            );
    }

    private applySelectedMeasurement(
        measure: DataLakeMeasure,
        resetQueryConfig: boolean,
        refreshData: boolean,
    ): void {
        const sourceConfig = this.sourceConfig;
        if (!sourceConfig) {
            return;
        }

        this.dataLakeMeasure = measure;
        this.dataLakeMeasureChange.emit(measure);
        sourceConfig.measureName = measure.measureName;
        sourceConfig.measure = measure;
        this.measurementInputValue = measure.measureName;

        if (!resetQueryConfig) {
            return;
        }

        sourceConfig.queryConfig.fields = [];
        if (this.fieldSelectionPanel) {
            this.fieldSelectionPanel.applyDefaultFields();
        }

        sourceConfig.queryConfig.groupBy = [];
        if (this.groupSelectionPanel) {
            this.groupSelectionPanel.applyDefaultFields();
        }

        if (refreshData) {
            this.triggerDataRefresh();
        }
    }

    private syncCurrentMeasure(): void {
        if (this.sourceConfig?.measure) {
            this.dataLakeMeasure = this.sourceConfig.measure;
            this.dataLakeMeasureChange.emit(this.sourceConfig.measure);
            this.measurementInputValue = this.sourceConfig.measure.measureName;
        } else if (this.sourceConfig?.measureName) {
            this.measurementInputValue = this.sourceConfig.measureName;
        }
    }

    changeDataAggregation() {
        this.fieldSelectionPanel.applyDefaultFields();
        this.triggerDataRefresh();
    }

    initializeSourceConfig(measureName = '') {
        this.dataConfig.sourceConfigs = [this.makeSourceConfig(measureName)];
    }

    makeSourceConfig(measureName = ''): SourceConfig {
        return {
            measureName,
            queryConfig: {
                selectedFilters: [],
                resultLabelOverrides: {},
                limit: 100,
                page: 1,
                aggregationTimeUnit: 'd',
                aggregationValue: 1,
                fill: 'none',
            },
            queryType: 'raw',
        };
    }

    makeVisualizationConfig(fields: FieldProvider): TableVisConfig {
        return {
            configurationValid: true,
            highlightedColumns: [],
            highlightedColumnColors: {},
            pageSize: 20,
            stickyHeaders: true,
            searchValue: '',
            selectedColumns: fields.allFields,
        };
    }

    createDefaultWidget(): void {
        if (this.checkIfDefaultTableShouldBeShown()) {
            const fields = this.fieldProviderService.generateFieldLists(
                this.dataConfig.sourceConfigs,
            );
            this.currentlyConfiguredWidget.visualizationConfig =
                this.makeVisualizationConfig(fields);
            this.currentlyConfiguredWidget.widgetType = 'table';
            this.widgetTypeService.notify({
                widgetId: this.currentlyConfiguredWidget.elementId,
                newWidgetTypeId: this.currentlyConfiguredWidget.widgetType,
            });

            this.createWidgetEmitter.emit({
                a: this.dataLakeMeasure,
                b: this.currentlyConfiguredWidget,
            });
        }
    }

    /**
     * This method checks if there is at least one data source and that no widget type is already configured.
     */
    checkIfDefaultTableShouldBeShown(): boolean {
        return (
            !!this.sourceConfig && !this.currentlyConfiguredWidget.widgetType
        );
    }

    triggerDataRefresh() {
        this.widgetConfigService.notify({
            refreshData: true,
            refreshView: true,
        });
    }

    toggleExpandFieldsDataSource() {
        this.expandFieldsDataSource = !this.expandFieldsDataSource;
    }

    toggleExpandFieldsQuery() {
        this.expandFieldsQuery = !this.expandFieldsQuery;
    }

    navigateToConnect(): void {
        this.router.navigate(['connect']);
    }

    navigateToPipelines(): void {
        this.router.navigate(['pipelines']);
    }
}
