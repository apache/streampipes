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
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
    inject,
} from '@angular/core';
import {
    DataExplorerDataConfig,
    DataExplorerWidgetModel,
    DatasetMeasure,
    DatasetSummaryDto,
    DatasetRestService,
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
    SearchSelectComponent,
    SplitSectionComponent,
} from '@streampipes/shared-ui';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { FormsModule } from '@angular/forms';
import { NgClass } from '@angular/common';
import { ClassDirective } from '@ngbracket/ngx-layout/extended';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatInput } from '@angular/material/input';
import { AggregateConfigurationComponent } from './aggregate-configuration/aggregate-configuration.component';
import { FillConfigurationComponent } from './fill-configuration/fill-configuration.component';
import { FilterSelectionPanelComponent } from './filter-selection-panel/filter-selection-panel.component';
import { OrderSelectionPanelComponent } from './order-selection-panel/order-selection-panel.component';
import { ResultLabelConfigurationComponent } from './result-label-configuration/result-label-configuration.component';
import { TranslatePipe } from '@ngx-translate/core';

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
        MatIcon,
        MatRadioGroup,
        FormsModule,
        NgClass,
        ClassDirective,
        MatRadioButton,
        FormFieldComponent,
        MatCheckbox,
        MatInput,
        SearchSelectComponent,
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
export class ChartDataSettingsComponent implements OnInit, OnDestroy {
    private datasetRestService = inject(DatasetRestService);
    private widgetConfigService = inject(ChartConfigurationService);
    private fieldProviderService = inject(ChartFieldProviderService);
    private widgetTypeService = inject(ChartTypeService);
    private router = inject(Router);
    private route = inject(ActivatedRoute);

    @Input() dataConfig: DataExplorerDataConfig;
    @Input() datasetMeasure: DatasetMeasure;
    @Input() newWidgetMode: boolean;
    @Input() widgetId: string;
    @Input() currentlyConfiguredWidget: DataExplorerWidgetModel;

    @Output() createWidgetEmitter: EventEmitter<
        Tuple2<DatasetMeasure, DataExplorerWidgetModel>
    > = new EventEmitter<Tuple2<DatasetMeasure, DataExplorerWidgetModel>>();
    @Output() datasetMeasureChange: EventEmitter<DatasetMeasure> =
        new EventEmitter<DatasetMeasure>();
    @Output() configureVisualizationEmitter: EventEmitter<void> =
        new EventEmitter<void>();

    @ViewChild('fieldSelectionPanel')
    fieldSelectionPanel: FieldSelectionPanelComponent;

    @ViewChild('groupSelectionPanel')
    groupSelectionPanel: GroupSelectionPanelComponent;

    availableDatasets: DatasetSummaryDto[] = [];
    selectedDataset: DatasetSummaryDto | undefined;

    private pendingDatasetRefresh = false;
    private dataRefreshTimeout?: ReturnType<typeof setTimeout>;

    step = 0;

    expandFieldsDataSource = true;
    expandFieldsQuery = true;

    get sourceConfig(): SourceConfig | undefined {
        return this.dataConfig?.sourceConfigs?.[0];
    }

    ngOnInit(): void {
        this.syncCurrentMeasure();
        this.loadPipelinesAndDatasets();
    }

    ngOnDestroy(): void {
        clearTimeout(this.dataRefreshTimeout);
    }

    loadPipelinesAndDatasets() {
        this.datasetRestService.getMeasurementSummary().subscribe(response => {
            this.availableDatasets = response.resources.sort((a, b) =>
                a.measureName.localeCompare(b.measureName),
            );
            this.syncSelectedDatasetSummary();

            if (!this.sourceConfig) {
                const defaultConfigs = this.findDefaultConfig();
                this.initializeSourceConfig(defaultConfigs.measureName);
                if (defaultConfigs.measureName !== undefined) {
                    this.selectedDataset = this.findDatasetSummary(
                        defaultConfigs.measureName,
                    );
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
        const matchingDataset = this.availableDatasets.find(
            dataset => dataset.measureName === measureNameFromQueryParams,
        );
        if (matchingDataset) {
            return {
                measureName: matchingDataset.measureName,
            };
        }

        if (this.availableDatasets.length > 0) {
            return {
                measureName: this.availableDatasets[0].measureName,
            };
        } else {
            return { measureName: undefined };
        }
    }

    updateMeasure(sourceConfig: SourceConfig, measureName: string) {
        sourceConfig.measureName = measureName;
        this.selectedDataset = this.findDatasetSummary(measureName);
        this.loadMeasurement(measureName, true, true);
    }

    onDatasetSelectionChange(
        sourceConfig: SourceConfig,
        selectedDataset: DatasetSummaryDto | DatasetSummaryDto[] | undefined,
    ): void {
        if (Array.isArray(selectedDataset)) {
            return;
        }

        if (!selectedDataset) {
            this.clearMeasure(sourceConfig);
            return;
        }

        this.updateMeasure(sourceConfig, selectedDataset.measureName);
    }

    private clearMeasure(sourceConfig: SourceConfig): void {
        sourceConfig.measureName = '';
        sourceConfig.measure = undefined;
        sourceConfig.queryConfig.fields = [];
        sourceConfig.queryConfig.groupBy = [];
        this.selectedDataset = undefined;
    }

    private loadMeasurement(
        measureName: string,
        resetQueryConfig: boolean,
        refreshData: boolean,
    ): void {
        this.datasetRestService
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
        measure: DatasetMeasure,
        resetQueryConfig: boolean,
        refreshData: boolean,
    ): void {
        const sourceConfig = this.sourceConfig;
        if (!sourceConfig) {
            return;
        }

        this.datasetMeasure = measure;
        this.datasetMeasureChange.emit(measure);
        sourceConfig.measureName = measure.measureName;
        sourceConfig.measure = measure;
        this.selectedDataset = this.findDatasetSummary(measure.measureName);

        if (!resetQueryConfig) {
            return;
        }

        this.pendingDatasetRefresh = refreshData;
        sourceConfig.queryConfig.fields = [];
        if (this.fieldSelectionPanel) {
            this.fieldSelectionPanel.applyDefaultFields();
        }

        sourceConfig.queryConfig.groupBy = [];
        if (this.groupSelectionPanel) {
            this.groupSelectionPanel.applyDefaultFields();
        }

        if (refreshData && this.fieldSelectionPanel) {
            this.scheduleDataRefresh();
        }
    }

    private syncCurrentMeasure(): void {
        if (this.sourceConfig?.measure) {
            this.datasetMeasure = this.sourceConfig.measure;
            this.datasetMeasureChange.emit(this.sourceConfig.measure);
            this.selectedDataset = this.findDatasetSummary(
                this.sourceConfig.measure.measureName,
            );
        } else if (this.sourceConfig?.measureName) {
            this.selectedDataset = this.findDatasetSummary(
                this.sourceConfig.measureName,
            );
        }
    }

    private syncSelectedDatasetSummary(): void {
        if (this.sourceConfig?.measureName) {
            this.selectedDataset = this.findDatasetSummary(
                this.sourceConfig.measureName,
            );
        }
    }

    private findDatasetSummary(
        measureName: string,
    ): DatasetSummaryDto | undefined {
        return this.availableDatasets.find(
            dataset => dataset.measureName === measureName,
        );
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

    onInitialFieldSelection(): void {
        const defaultWidgetCreated = this.createDefaultWidget();
        if (defaultWidgetCreated || this.pendingDatasetRefresh) {
            this.scheduleDataRefresh();
        }
    }

    createDefaultWidget(): boolean {
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
                deferInitialDataLoad: true,
            });

            this.createWidgetEmitter.emit({
                a: this.datasetMeasure,
                b: this.currentlyConfiguredWidget,
            });

            return true;
        }

        return false;
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

    private scheduleDataRefresh(): void {
        this.pendingDatasetRefresh = false;
        clearTimeout(this.dataRefreshTimeout);
        this.dataRefreshTimeout = setTimeout(() => this.triggerDataRefresh());
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
