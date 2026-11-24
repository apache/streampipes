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
    ElementRef,
    computed,
    signal,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
    inject,
    ChangeDetectionStrategy,
} from '@angular/core';
import {
    DataExplorerDataConfig,
    DataExplorerWidgetModel,
    DatasetMetadata,
    DatasetSummaryDto,
    DatalakeRestService,
    SourceConfig,
    SpAsset,
    EventPropertyUnion,
    FilterExpressionGroup,
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
import { MatButton } from '@angular/material/button';
import {
    FormFieldComponent,
    SpAlertBannerComponent,
    SpAssetBrowserService,
    SearchSelectComponent,
} from '@streampipes/shared-ui';
import { Subscription } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatFormField } from '@angular/material/form-field';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { FormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatInput } from '@angular/material/input';
import { AggregateConfigurationComponent } from './aggregate-configuration/aggregate-configuration.component';
import { FillConfigurationComponent } from './fill-configuration/fill-configuration.component';
import { FilterSelectionPanelComponent } from './filter-selection-panel/filter-selection-panel.component';
import { OrderSelectionPanelComponent } from './order-selection-panel/order-selection-panel.component';
import { ResultLabelConfigurationComponent } from './result-label-configuration/result-label-configuration.component';
import { TranslatePipe } from '@ngx-translate/core';
import { ChartSettingsSectionComponent } from '../../../../../chart-shared/components/chart-config/settings-section.component';

interface DatasetAssetOption {
    id: string;
    label: string;
    paths: Map<string, string[]>;
}

@Component({
    selector: 'sp-chart-data-settings',
    host: { class: 'flex-row' },
    templateUrl: './chart-data-settings.component.html',
    styleUrls: ['./chart-data-settings.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        ChartSettingsSectionComponent,

        SpAlertBannerComponent,
        MatButton,
        MatFormField,
        MatRadioGroup,
        FormsModule,
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
    private datalakeRestService = inject(DatalakeRestService);
    private widgetConfigService = inject(ChartConfigurationService);
    private fieldProviderService = inject(ChartFieldProviderService);
    private widgetTypeService = inject(ChartTypeService);
    private router = inject(Router);
    private route = inject(ActivatedRoute);

    @Input() dataConfig: DataExplorerDataConfig;
    @Input() dataLakeMeasure: DatasetMetadata;
    @Input() newWidgetMode: boolean;
    @Input() widgetId: string;
    @Input() currentlyConfiguredWidget: DataExplorerWidgetModel;

    @Output() createWidgetEmitter: EventEmitter<
        Tuple2<DatasetMetadata, DataExplorerWidgetModel>
    > = new EventEmitter<Tuple2<DatasetMetadata, DataExplorerWidgetModel>>();
    @Output() dataLakeMeasureChange: EventEmitter<DatasetMetadata> =
        new EventEmitter<DatasetMetadata>();
    @Output() configureVisualizationEmitter: EventEmitter<void> =
        new EventEmitter<void>();

    @ViewChild('fieldSelectionPanel')
    fieldSelectionPanel: FieldSelectionPanelComponent;

    @ViewChild('groupSelectionPanel')
    groupSelectionPanel: GroupSelectionPanelComponent;

    readonly availableDatasets = signal<DatasetSummaryDto[]>([]);
    private readonly assetData = toSignal(
        inject(SpAssetBrowserService).assetData$,
    );
    readonly datasetAssets = computed(() => {
        const datasetIds = new Set(
            this.availableDatasets().map(dataset => dataset.elementId),
        );
        const options: DatasetAssetOption[] = [];
        const visit = (
            asset: SpAsset,
            parentPath: string,
        ): Map<string, string[]> => {
            const label = parentPath
                ? parentPath + ' / ' + asset.assetName
                : asset.assetName;
            const paths = new Map<string, string[]>();
            for (const link of asset.assetLinks ?? []) {
                if (
                    link.linkType === 'dataset' &&
                    datasetIds.has(link.resourceId)
                ) {
                    paths.set(link.resourceId, [label]);
                }
            }
            for (const child of asset.assets ?? []) {
                for (const [id, childPaths] of visit(child, label)) {
                    paths.set(id, [...(paths.get(id) ?? []), ...childPaths]);
                }
            }
            if (paths.size) options.push({ id: asset.assetId, label, paths });
            return paths;
        };
        for (const asset of this.assetData()?.assets ?? []) {
            visit(asset, '');
        }
        return options.sort((a, b) => a.label.localeCompare(b.label));
    });
    browsingAssets = false;
    readonly selectedAssetId = signal<string | undefined>(undefined);
    readonly selectedAsset = computed(() =>
        this.datasetAssets().find(asset => asset.id === this.selectedAssetId()),
    );
    readonly assetSearch = signal('');
    readonly filteredAssetDatasets = computed(() => {
        const query = this.assetSearch().trim().toLocaleLowerCase();
        return this.assetDatasets().filter(dataset =>
            dataset.measureName.toLocaleLowerCase().includes(query),
        );
    });
    readonly assetDatasets = computed(() => {
        const asset = this.selectedAsset();
        return this.availableDatasets().filter(dataset =>
            asset?.paths.has(dataset.elementId),
        );
    });

    selectedDataset: DatasetSummaryDto | undefined;

    reviewFields: string[] = [];
    filtersRemoved = false;
    private measurementRequest?: Subscription;
    private pendingDatasetRefresh = false;
    private dataRefreshTimeout?: ReturnType<typeof setTimeout>;

    get sourceConfig(): SourceConfig | undefined {
        return this.dataConfig?.sourceConfigs?.[0];
    }

    ngOnInit(): void {
        this.syncCurrentMeasure();
        this.loadPipelinesAndDatasets();
    }

    ngOnDestroy(): void {
        this.measurementRequest?.unsubscribe();
        clearTimeout(this.dataRefreshTimeout);
    }

    loadPipelinesAndDatasets() {
        this.datalakeRestService.getMeasurementSummary().subscribe(response => {
            this.availableDatasets.set(
                response.resources.sort((a, b) =>
                    a.measureName.localeCompare(b.measureName),
                ),
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
        const matchingDataset = this.availableDatasets().find(
            dataset => dataset.measureName === measureNameFromQueryParams,
        );
        if (matchingDataset) {
            return {
                measureName: matchingDataset.measureName,
            };
        }

        if (this.availableDatasets().length > 0) {
            return {
                measureName: this.availableDatasets()[0].measureName,
            };
        } else {
            return { measureName: undefined };
        }
    }

    private readonly host = inject<ElementRef<HTMLElement>>(ElementRef);

    focusFields(): void {
        const section =
            this.host.nativeElement.querySelector<HTMLDetailsElement>(
                'sp-field-selection-panel details',
            );
        if (section) {
            section.open = true;
            section.querySelector('summary')?.focus();
        } else {
            this.host.nativeElement
                .querySelector<HTMLInputElement>('sp-search-select input')
                ?.focus();
        }
    }

    selectAsset(
        asset: DatasetAssetOption | DatasetAssetOption[] | undefined,
    ): void {
        this.assetSearch.set('');
        this.selectedAssetId.set(Array.isArray(asset) ? undefined : asset?.id);
    }

    toggleAssetBrowser(): void {
        this.assetSearch.set('');
        this.selectedAssetId.set(undefined);
        this.browsingAssets = !this.browsingAssets;
    }

    updateMeasure(sourceConfig: SourceConfig, measureName: string) {
        this.measurementRequest?.unsubscribe();
        if (sourceConfig.measure?.measureName === measureName) {
            this.syncSelectedDatasetSummary();
            return;
        }
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

        this.browsingAssets = false;
        this.assetSearch.set('');
        this.selectedAssetId.set(undefined);
        this.updateMeasure(sourceConfig, selectedDataset.measureName);
    }

    private clearMeasure(sourceConfig: SourceConfig): void {
        this.measurementRequest?.unsubscribe();
        this.reviewFields = [];
        this.filtersRemoved = false;
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
        this.measurementRequest?.unsubscribe();
        this.measurementRequest = this.datalakeRestService
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
        measure: DatasetMetadata,
        resetQueryConfig: boolean,
        refreshData: boolean,
    ): void {
        const sourceConfig = this.sourceConfig;
        if (!sourceConfig) {
            return;
        }

        const previousMeasure = sourceConfig.measure;
        if (resetQueryConfig && previousMeasure) {
            this.reconcileDataset(sourceConfig, previousMeasure, measure);
        }
        this.dataLakeMeasure = measure;
        this.dataLakeMeasureChange.emit(measure);
        sourceConfig.measureName = measure.measureName;
        sourceConfig.measure = measure;
        this.selectedDataset = this.findDatasetSummary(measure.measureName);

        if (!resetQueryConfig) {
            return;
        }

        this.pendingDatasetRefresh = refreshData;
        sourceConfig.queryConfig.fields ??= [];
        if (this.fieldSelectionPanel) {
            this.fieldSelectionPanel.applyDefaultFields(!previousMeasure);
        }

        if (this.groupSelectionPanel) {
            this.groupSelectionPanel.applyDefaultFields();
        }

        if (refreshData && this.fieldSelectionPanel) {
            this.scheduleDataRefresh();
        }
    }

    private reconcileDataset(
        source: SourceConfig,
        previous: DatasetMetadata,
        next: DatasetMetadata,
    ): void {
        const properties = (measure: DatasetMetadata) =>
            new Map(
                measure.eventSchema.eventProperties.map(
                    property => [property.runtimeName, property] as const,
                ),
            );
        const before = properties(previous);
        const after = properties(next);
        const affected = new Set<string>();
        const compatible = (name: string): boolean => {
            const old = before.get(name);
            const current = after.get(name);
            if (!old || !current) return false;
            const type = (p: EventPropertyUnion) =>
                'runtimeType' in p ? p.runtimeType : p['@class'];
            return (
                (type(old) === type(current) ||
                    (this.fieldProviderService.isNumber(old) &&
                        this.fieldProviderService.isNumber(current))) &&
                old.propertyScope === current.propertyScope
            );
        };
        const query = source.queryConfig;
        query.fields = (query.fields ?? []).filter(field => {
            if (compatible(field.runtimeName)) {
                if (
                    field.selected &&
                    before.get(field.runtimeName)?.semanticType !==
                        after.get(field.runtimeName)?.semanticType
                )
                    affected.add(field.runtimeName);
                return true;
            }
            if (field.selected) affected.add(field.runtimeName);
            return false;
        });
        query.groupBy = (query.groupBy ?? []).filter(field => {
            if (compatible(field.runtimeName)) return true;
            if (field.selected) affected.add(field.runtimeName);
            return false;
        });
        this.filtersRemoved = false;
        query.selectedFilters = (query.selectedFilters ?? []).filter(filter => {
            const name = filter.field?.runtimeName;
            if (!name) return true;
            if (compatible(name)) {
                filter.field = { ...filter.field, measure: next.measureName };
                return true;
            }
            this.filtersRemoved = true;
            affected.add(name);
            return false;
        });
        const checkExpression = (group: FilterExpressionGroup): boolean => {
            let valid = true;
            for (const child of group.children) {
                if (child.type === 'group') {
                    if (!checkExpression(child)) valid = false;
                } else if (!compatible(child.field)) {
                    affected.add(child.field);
                    valid = false;
                }
            }
            return valid;
        };
        if (
            query.filterExpression &&
            !checkExpression(query.filterExpression)
        ) {
            this.filtersRemoved = true;
            delete query.filterExpression;
        }
        this.reviewFields = [...affected];
    }

    private syncCurrentMeasure(): void {
        if (this.sourceConfig?.measure) {
            this.dataLakeMeasure = this.sourceConfig.measure;
            this.dataLakeMeasureChange.emit(this.sourceConfig.measure);
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
        return this.availableDatasets().find(
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
                a: this.dataLakeMeasure,
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

    navigateToConnect(): void {
        this.router.navigate(['connect']);
    }

    navigateToPipelines(): void {
        this.router.navigate(['pipelines']);
    }
}
