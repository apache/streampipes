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
    AfterViewInit,
    Component,
    ComponentFactoryResolver,
    ComponentRef,
    ElementRef,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Output,
    SimpleChanges,
    ViewChild,
    inject,
} from '@angular/core';
import {
    ClientDashboardItem,
    DataExplorerWidgetModel,
    DataLakeMeasure,
    ExtendedTimeSettings,
    QuickTimeSelection,
    SpLogMessage,
    SpQueryResult,
    TimeSelectionConstants,
    TimeSettings,
} from '@streampipes/platform-services';
import { interval, Subscription } from 'rxjs';
import { takeWhile } from 'rxjs/operators';
import { ChartRegistry } from '../../registry/chart-registry.service';
import { ChartDirective } from './chart.directive';
import { ChartTypeService } from '../../services/chart-type.service';
import { AuthService } from '../../../services/auth.service';
import { UserPrivilege } from '../../../core/auth/user-privilege.enum';
import {
    CurrentUserService,
    NameChangeService,
    SpExceptionMessageComponent,
    SpLabelComponent,
    TimeRangeSelectorMenuComponent,
    TimeSelectionService,
    TimeSelectorLabel,
} from '@streampipes/shared-ui';
import { ChartSharedService } from '../../services/chart-shared.service';
import {
    BaseWidgetData,
    DashboardChartOverrides,
    ObservableGenerator,
} from '../../models/dataview-dashboard.model';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { ResizeService } from '../../services/resize.service';
import { NgStyle } from '@angular/common';
import { StyleDirective } from '@ngbracket/ngx-layout/extended';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'sp-chart-container',
    templateUrl: './chart-container.component.html',
    styleUrls: ['./chart-container.component.scss'],
    imports: [
        NgStyle,
        StyleDirective,
        FlexDirective,
        LayoutDirective,
        LayoutAlignDirective,
        MatIconButton,
        MatIcon,
        FormsModule,
        MatProgressSpinner,
        SpLabelComponent,
        MatMenuTrigger,
        MatTooltip,
        MatMenu,
        MatMenuItem,
        TimeRangeSelectorMenuComponent,
        MatButton,
        ChartDirective,
        SpExceptionMessageComponent,
        TranslatePipe,
    ],
})
export class ChartContainerComponent
    implements OnInit, OnDestroy, OnChanges, AfterViewInit
{
    private chartRegistryService = inject(ChartRegistry);
    private dashboardService = inject(ChartSharedService);
    private componentFactoryResolver = inject(ComponentFactoryResolver);
    private widgetTypeService = inject(ChartTypeService);
    private authService = inject(AuthService);
    private currentUserService = inject(CurrentUserService);
    private timeSelectionService = inject(TimeSelectionService);
    private nameChangeService = inject(NameChangeService);
    private el = inject<ElementRef<HTMLDivElement>>(ElementRef);
    private resizeService = inject(ResizeService);

    @ViewChild('menuTrigger') menu: MatMenuTrigger;
    @ViewChild('timeSelectorMenu')
    timeSelectorMenu: TimeRangeSelectorMenuComponent;
    @Input()
    dashboardItem: ClientDashboardItem;

    @Input()
    configuredWidget: DataExplorerWidgetModel;

    @Input()
    dataLakeMeasure: DataLakeMeasure;

    @Input()
    editMode: boolean;

    @Input()
    dataViewMode = false;

    @Input()
    previewMode = false;

    @Input()
    gridMode = true;

    @Input()
    kioskMode = false;

    @Input()
    widgetIndex: number;

    /**
     * This is the date range (start, end) to view the data and is set in data-explorer.ts
     */
    @Input()
    timeSettings: TimeSettings;

    @Input()
    globalTimeEnabled = true;

    @Input()
    observableGenerator: ObservableGenerator;

    @Input()
    dashboardChartOverrides: DashboardChartOverrides = {};

    @Output() deleteCallback: EventEmitter<number> = new EventEmitter<number>();
    @Output() startEditModeEmitter: EventEmitter<DataExplorerWidgetModel> =
        new EventEmitter<DataExplorerWidgetModel>();
    @Output() queryResultsEmitter: EventEmitter<SpQueryResult[]> =
        new EventEmitter<SpQueryResult[]>();

    title = '';
    widgetLoaded = false;
    timerActive = false;
    loadingTime = 0;
    isEditingName = false;
    tempName = '';

    quickSelections: QuickTimeSelection[];
    labels: TimeSelectorLabel;
    clonedTimeSettings: TimeSettings;
    timeSettingsModified: boolean = false;
    enableTimePicker: boolean = true;
    maxDayRange: number = 0;
    tooltipText: string;
    dateFormat: Intl.DateTimeFormatOptions = {
        weekday: 'short',
        year: 'numeric',
        month: 'numeric',
        day: 'numeric',
    };

    hasDataExplorerWritePrivileges = false;
    hasDashboardWritePrivileges = false;

    auth$: Subscription;
    widgetTypeChanged$: Subscription;
    interval$: Subscription;

    errorMessage: SpLogMessage;

    componentRef: ComponentRef<BaseWidgetData<any>>;

    @ViewChild(ChartDirective, { static: true }) widgetHost!: ChartDirective;

    resizeObserver: ResizeObserver;
    resizeTimeout: any;

    ngAfterViewInit(): void {
        const container = this.el.nativeElement.querySelector(
            '.widget-content',
        ) as HTMLDivElement;
        this.resizeObserver = new ResizeObserver(entries => {
            clearTimeout(this.resizeTimeout);
            this.resizeTimeout = setTimeout(() => {
                const { width, height } =
                    entries[entries.length - 1].contentRect;

                this.resizeService.notify({
                    width,
                    height,
                    widgetId: this.dashboardItem?.id || undefined,
                });
            }, 100);
        });
        this.resizeObserver.observe(container);
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.widgetIndex && this.componentRef?.instance) {
            this.componentRef.instance.widgetIndex =
                changes.widgetIndex.currentValue;
        }
        if (changes.dashboardChartOverrides && this.componentRef?.instance) {
            this.componentRef.instance.dashboardChartOverrides =
                changes.dashboardChartOverrides.currentValue;
            (this.componentRef.instance as any).refreshView?.();
        }
        if (
            (changes.globalTimeEnabled || changes.timeSettings) &&
            this.componentRef?.instance
        ) {
            this.componentRef.instance.timeSettings = this.getTimeSettings();
            (this.componentRef.instance as any).updateData?.();
        }
    }

    ngOnInit(): void {
        this.quickSelections ??=
            this.timeSelectionService.defaultQuickTimeSelections;
        this.labels ??= this.timeSelectionService.defaultLabels;
        this.auth$ = this.currentUserService.user$.subscribe(user => {
            this.hasDataExplorerWritePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW,
            );
            this.hasDashboardWritePrivileges = this.authService.hasRole(
                UserPrivilege.PRIVILEGE_WRITE_DASHBOARD,
            );
        });
        this.widgetLoaded = true;
        this.title = this.dataLakeMeasure?.measureName;
        this.widgetTypeChanged$ =
            this.widgetTypeService.chartTypeChangeSubject.subscribe(
                typeChange => {
                    if (
                        typeChange.widgetId === this.configuredWidget.elementId
                    ) {
                        this.chooseWidget(typeChange.newWidgetTypeId);
                    }
                },
            );
        this.chooseWidget(this.configuredWidget.widgetType);
        this.timeSelectionService.updateTimeSettings(
            this.quickSelections,
            this.getTimeSettings(),
            new Date(),
        );
        if (
            this.dashboardItem?.timeSettings !== undefined &&
            this.dashboardItem?.timeSettings !== null
        ) {
            this.clonedTimeSettings = {
                startTime: this.dashboardItem?.timeSettings.startTime,
                endTime: this.dashboardItem?.timeSettings.endTime,
                timeSelectionId:
                    this.dashboardItem?.timeSettings.timeSelectionId,
            };
        } else {
            this.clonedTimeSettings = {
                startTime: this.configuredWidget.timeSettings.startTime,
                endTime: this.configuredWidget.timeSettings.endTime,
                timeSelectionId:
                    this.configuredWidget.timeSettings.timeSelectionId,
            };
        }

        if (
            this.dashboardItem?.timeSettings !== undefined &&
            this.dashboardItem?.timeSettings !== null &&
            this.dashboardItem?.timeSettings?.timeSelectionId !==
                this.configuredWidget.timeSettings.timeSelectionId
        ) {
            this.timeSettingsModified = true;
        }
        this.createDateStringToolTip(this.getTimeSettings());
    }

    ngOnDestroy() {
        this.resizeObserver?.disconnect();
        this.componentRef?.destroy();
        this.auth$?.unsubscribe();
        this.widgetTypeChanged$?.unsubscribe();
        this.interval$?.unsubscribe();
    }

    chooseWidget(widgetTypeId: string) {
        if (widgetTypeId != undefined) {
            const widgetToDisplay =
                this.chartRegistryService.getChartTemplate(widgetTypeId);
            this.loadComponent(widgetToDisplay.widgetComponent);
        }
    }

    loadComponent(widgetToDisplay) {
        const container = this.el.nativeElement.querySelector(
            '.widget-content',
        ) as HTMLDivElement;
        const initialSize = {
            width: container.clientWidth,
            height: container.clientHeight,
        };
        const componentFactory =
            this.componentFactoryResolver.resolveComponentFactory<
                BaseWidgetData<any>
            >(widgetToDisplay);

        const viewContainerRef = this.widgetHost.viewContainerRef;
        viewContainerRef.clear();

        this.componentRef =
            viewContainerRef.createComponent<BaseWidgetData<any>>(
                componentFactory,
            );
        this.componentRef.instance.dataExplorerWidget = this.configuredWidget;
        this.componentRef.instance.initialSize = initialSize;
        this.componentRef.instance.timeSettings = this.getTimeSettings();
        this.timeSelectionService.updateTimeSettings(
            this.quickSelections,
            this.getTimeSettings(),
            new Date(),
        );
        this.componentRef.instance.dataViewMode = this.dataViewMode;
        this.componentRef.instance.editMode = this.editMode;
        this.componentRef.instance.kioskMode = this.kioskMode;
        this.componentRef.instance.dataViewDashboardItem = this.dashboardItem;
        this.componentRef.instance.dataExplorerWidget = this.configuredWidget;
        this.componentRef.instance.previewMode = this.previewMode;
        this.componentRef.instance.gridMode = this.gridMode;
        this.componentRef.instance.widgetIndex = this.widgetIndex;
        this.componentRef.instance.observableGenerator =
            this.observableGenerator;
        this.componentRef.instance.dashboardChartOverrides =
            this.dashboardChartOverrides;
        const remove$ =
            this.componentRef.instance.removeWidgetCallback.subscribe(ev =>
                this.removeWidget(),
            );
        const timer$ = this.componentRef.instance.timerCallback.subscribe(ev =>
            this.handleTimer(ev),
        );
        const error$ = this.componentRef.instance.errorCallback.subscribe(
            ev => {
                this.errorMessage = ev;
                if (ev) {
                    this.queryResultsEmitter.emit([]);
                }
            },
        );
        const data$ = this.componentRef.instance.dataReceivedCallback.subscribe(
            results => this.queryResultsEmitter.emit(results),
        );

        this.componentRef.onDestroy(destroy => {
            this.componentRef.instance.cleanupSubscriptions();
            remove$?.unsubscribe();
            timer$?.unsubscribe();
            error$?.unsubscribe();
            data$?.unsubscribe();
        });
    }

    getTimeSettings(): TimeSettings {
        if (this.globalTimeEnabled) {
            return this.timeSettings;
        } else if (
            this.dashboardItem.timeSettings !== undefined &&
            this.dashboardItem.timeSettings !== null
        ) {
            return this.dashboardItem.timeSettings as TimeSettings;
        } else {
            return this.configuredWidget.timeSettings as TimeSettings;
        }
    }

    removeWidget() {
        this.deleteCallback.emit(this.widgetIndex);
    }

    startEditMode() {
        this.startEditModeEmitter.emit(this.configuredWidget);
    }

    startLoadingTimer() {
        this.timerActive = true;
        this.interval$ = interval(100)
            .pipe(takeWhile(() => this.timerActive))
            .subscribe(value => {
                this.loadingTime = (value * 100) / 1000;
            });
    }

    stopLoadingTimer() {
        this.timerActive = false;
        this.interval$.unsubscribe();
    }

    handleTimer(start: boolean) {
        if (start) {
            this.startLoadingTimer();
        } else {
            this.stopLoadingTimer();
        }
    }

    downloadDataAsFile(): void {
        this.dashboardService.downloadDataAsFile(
            this.timeSettings,
            this.configuredWidget,
        );
    }

    modifyWidgetTimeSettings(extendedTimeSettings: ExtendedTimeSettings): void {
        this.dashboardItem.timeSettings = extendedTimeSettings.timeSettings;
        this.timeSelectionService.notify(
            extendedTimeSettings.timeSettings,
            this.widgetIndex,
        );
        this.menu.closeMenu();
        this.timeSettingsModified = true;
        this.createDateStringToolTip(this.getTimeSettings());
    }

    resetWidgetTimeSettings(): void {
        this.dashboardItem.timeSettings = undefined;
        this.timeSelectionService.updateTimeSettings(
            this.quickSelections,
            this.getTimeSettings(),
            new Date(),
        );
        this.clonedTimeSettings = {
            startTime: this.configuredWidget.timeSettings.startTime,
            endTime: this.configuredWidget.timeSettings.endTime,
            timeSelectionId: this.configuredWidget.timeSettings.timeSelectionId,
        };
        this.timeSelectionService.notify(
            this.getTimeSettings(),
            this.widgetIndex,
        );
        this.menu.closeMenu();
        this.timeSettingsModified = false;
        this.createDateStringToolTip(this.getTimeSettings());
        setTimeout(() => this.timeSelectorMenu.triggerDisplayUpdate());
    }

    createDateStringToolTip(timeSettings: TimeSettings): void {
        if (timeSettings.timeSelectionId !== TimeSelectionConstants.CUSTOM) {
            this.tooltipText = this.timeSelectionService.getTimeSelection(
                this.quickSelections,
                timeSettings.timeSelectionId,
            ).label;
        } else {
            const startDate = new Date(timeSettings.startTime);
            const endDate = new Date(timeSettings.endTime);
            const timeString = {
                startDate: this.timeSelectionService.formatDate(
                    startDate,
                    this.enableTimePicker,
                    this.dateFormat,
                ),
                endDate: this.timeSelectionService.formatDate(
                    endDate,
                    this.enableTimePicker,
                    this.dateFormat,
                ),
                startTime: startDate.toLocaleTimeString(),
                endTime: endDate.toLocaleTimeString(),
            };

            this.tooltipText = `${timeString.startDate} ${timeString.startTime} - ${timeString.endDate} ${timeString.endTime}`;
        }
    }

    startEditingName() {
        this.tempName = this.dashboardItem?.name || '';
        this.isEditingName = true;
    }

    saveName() {
        if (!this.dashboardItem) return;
        this.dashboardItem.name = this.tempName.trim();
        this.nameChangeService.notify(
            this.dashboardItem.id,
            this.tempName.trim(),
        );

        this.isEditingName = false;
    }

    cancelEditingName() {
        this.isEditingName = false;
    }
}
