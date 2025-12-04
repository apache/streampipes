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
    Directive,
    EventEmitter,
    HostBinding,
    inject,
    Input,
    OnInit,
    Output,
} from '@angular/core';
import { ChartConfigurationService } from '../../../services/chart-configuration.service';
import {
    ClientDashboardItem,
    DataExplorerDataConfig,
    DataExplorerField,
    DataExplorerWidgetModel,
    SpLogMessage,
    SpQueryResult,
    TimeSettings,
} from '@streampipes/platform-services';
import { ResizeService } from '../../../services/resize.service';
import {
    BaseWidgetData,
    FieldProvider,
    ObservableGenerator,
} from '../../../models/dataview-dashboard.model';
import { Observable, Subject, Subscription, zip } from 'rxjs';
import { ChartFieldProviderService } from '../../../services/chart-field-provider.service';
import { catchError, switchMap } from 'rxjs/operators';
import { DataExplorerChartRegistry } from '../../../registry/data-explorer-chart-registry';
import { SpFieldUpdateService } from '../../../services/field-update.service';
import { TimeSelectionService } from '@streampipes/shared-ui';
import { WidgetSize } from '../../../models/dataset.model';

@Directive()
export abstract class BaseDataExplorerWidgetDirective<
        T extends DataExplorerWidgetModel,
    >
    implements BaseWidgetData<T>, OnInit
{
    private static TOO_MUCH_DATA_PARAMETER = 10000;

    @Output()
    removeWidgetCallback: EventEmitter<boolean> = new EventEmitter();

    @Output()
    timerCallback: EventEmitter<boolean> = new EventEmitter();

    @Output()
    errorCallback: EventEmitter<SpLogMessage> =
        new EventEmitter<SpLogMessage>();

    @Input() editMode: boolean;
    @Input() kioskMode: boolean;
    @Input() dataViewMode: boolean;
    @Input() observableGenerator: ObservableGenerator;

    @Input() timeSettings: TimeSettings;

    @Input()
    previewMode = false;

    @Input()
    gridMode = true;

    @Input() dataViewDashboardItem: ClientDashboardItem;
    @Input() dataExplorerWidget: T;

    @Input()
    widgetIndex: number;

    @HostBinding('class') className = 'h-100';

    @Input()
    initialSize: WidgetSize;

    currentWidth: number;
    currentHeight: number;

    public selectedProperties: string[];

    public showNoDataInDateRange: boolean;
    public showData: boolean;
    public showIsLoadingData: boolean;
    public showTooMuchData: boolean;
    public showInvalidConfiguration = false;
    public amountOfTooMuchEvents: number;

    fieldProvider: FieldProvider;

    widgetConfiguration$: Subscription;
    resize$: Subscription;
    timeSelection$: Subscription;

    requestQueue$: Subject<Observable<SpQueryResult>[]> = new Subject<
        Observable<SpQueryResult>[]
    >();

    protected widgetConfigurationService = inject(ChartConfigurationService);
    protected resizeService = inject(ResizeService);
    protected timeSelectionService = inject(TimeSelectionService);
    protected widgetRegistryService = inject(DataExplorerChartRegistry);
    protected fieldUpdateService = inject(SpFieldUpdateService);
    public fieldService = inject(ChartFieldProviderService);

    ngOnInit(): void {
        this.currentWidth = this.initialSize.width;
        this.currentHeight = this.initialSize.height;
        this.showData = true;
        const sourceConfigs = this.dataExplorerWidget.dataConfig.sourceConfigs;
        this.fieldProvider =
            this.fieldService.generateFieldLists(sourceConfigs);

        this.requestQueue$
            .pipe(
                switchMap(observables => {
                    this.errorCallback.emit(undefined);
                    return zip(...observables).pipe(
                        catchError(err => {
                            this.timerCallback.emit(false);
                            this.errorCallback.emit(err.error);
                            return [];
                        }),
                    );
                }),
            )
            .subscribe(results => {
                results.forEach(
                    (result, index) => (result.sourceIndex = index),
                );
                this.timerCallback.emit(false);
                setTimeout(() => {
                    this.validateReceivedData(results);
                });
            });

        this.widgetConfiguration$ =
            this.widgetConfigurationService.configurationChangedSubject.subscribe(
                refreshMessage => {
                    if (refreshMessage.refreshData) {
                        const newFieldsProvider =
                            this.fieldService.generateFieldLists(sourceConfigs);
                        const addedFields = this.fieldService.getAddedFields(
                            this.fieldProvider.allFields,
                            newFieldsProvider.allFields,
                        );
                        const removedFields =
                            this.fieldService.getRemovedFields(
                                this.fieldProvider.allFields,
                                newFieldsProvider.allFields,
                            );
                        this.fieldProvider =
                            this.fieldService.generateFieldLists(sourceConfigs);
                        this.handleUpdatedFields(addedFields, removedFields);
                        this.updateData();
                    }
                    if (refreshMessage.refreshView) {
                        this.refreshView();
                    }
                },
            );
        if (!this.previewMode) {
            this.resize$ = this.resizeService.resizeSubject.subscribe(info => {
                if (
                    this.dataViewMode ||
                    info.widgetId === this.dataViewDashboardItem.id
                ) {
                    this.currentWidth = info.width;
                    this.currentHeight = info.height;
                    this.onResize(info.width, info.height);
                }
            });
        }
        this.timeSelection$ =
            this.timeSelectionService.timeSelectionChangeSubject.subscribe(
                widgetTimeSettings => {
                    if (
                        widgetTimeSettings.widgetIndex === undefined ||
                        widgetTimeSettings.widgetIndex === this.widgetIndex
                    ) {
                        if (widgetTimeSettings.timeSettings) {
                            this.timeSettings = widgetTimeSettings.timeSettings;
                        } else {
                            this.timeSelectionService.updateTimeSettings(
                                this.timeSelectionService
                                    .defaultQuickTimeSelections,
                                this.timeSettings,
                                new Date(),
                            );
                        }
                        this.updateData();
                    }
                },
            );
        this.updateData();
    }

    public cleanupSubscriptions(): void {
        this.widgetConfiguration$?.unsubscribe();
        this.resize$?.unsubscribe();
        this.timeSelection$.unsubscribe();
        this.requestQueue$?.unsubscribe();
    }

    public setShownComponents(
        showNoDataInDateRange: boolean,
        showData: boolean,
        showIsLoadingData: boolean,
        showTooMuchData: boolean = false,
    ) {
        this.showNoDataInDateRange = showNoDataInDateRange;
        this.showData = showData;
        this.showIsLoadingData = showIsLoadingData;
        this.showTooMuchData = showTooMuchData;
    }

    public updateData(includeTooMuchEventsParameter: boolean = true) {
        this.beforeDataFetched();
        this.loadData(includeTooMuchEventsParameter);
    }

    private loadData(includeTooMuchEventsParameter: boolean) {
        const returnCompleteResult =
            includeTooMuchEventsParameter &&
            !this.dataExplorerWidget.dataConfig.ignoreTooMuchDataWarning;
        const observables = this.observableGenerator.generateObservables(
            this.timeSettings.startTime,
            this.timeSettings.endTime,
            this.dataExplorerWidget.dataConfig as DataExplorerDataConfig,
            this.dataExplorerWidget.elementId,
            returnCompleteResult
                ? BaseDataExplorerWidgetDirective.TOO_MUCH_DATA_PARAMETER
                : undefined,
        );
        this.timerCallback.emit(true);
        this.requestQueue$.next(observables);
    }

    validateReceivedData(spQueryResults: SpQueryResult[]) {
        const spQueryResult = spQueryResults[0];

        if (spQueryResult.total === 0) {
            this.setShownComponents(true, false, false, false);
        } else if (spQueryResult['spQueryStatus'] === 'TOO_MUCH_DATA') {
            this.amountOfTooMuchEvents = spQueryResult.total;
            this.setShownComponents(false, false, false, true);
        } else {
            this.onDataReceived(spQueryResults);
        }
    }

    loadDataWithTooManyEvents() {
        this.updateData(false);
    }

    getColumnIndex(field: DataExplorerField, data: SpQueryResult) {
        return data.headers.indexOf(field.fullDbName);
    }

    public abstract refreshView(): void;

    public abstract beforeDataFetched(): void;

    public abstract onDataReceived(spQueryResult: SpQueryResult[]): void;

    public abstract onResize(width: number, height: number): void;

    protected abstract handleUpdatedFields(
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
    ): void;
}
