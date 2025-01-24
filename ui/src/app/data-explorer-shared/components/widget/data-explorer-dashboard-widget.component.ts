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
    ComponentFactoryResolver,
    ComponentRef,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Output,
    SimpleChanges,
    ViewChild,
} from '@angular/core';
import { GridsterItemComponent } from 'angular-gridster2';
import {
    DashboardItem,
    DataExplorerWidgetModel,
    DataLakeMeasure,
    ExtendedTimeSettings,
    QuickTimeSelection,
    SpLogMessage,
    TimeSelectionConstants,
    TimeSettings,
} from '@streampipes/platform-services';
import { interval, Subscription } from 'rxjs';
import { takeWhile } from 'rxjs/operators';
import { DataExplorerWidgetRegistry } from '../../registry/data-explorer-widget-registry';
import { WidgetDirective } from './widget.directive';
import { WidgetTypeService } from '../../services/widget-type.service';
import { AuthService } from '../../../services/auth.service';
import { UserPrivilege } from '../../../_enums/user-privilege.enum';
import {
    CurrentUserService,
    TimeRangeSelectorMenuComponent,
    TimeSelectionService,
    TimeSelectorLabel,
} from '@streampipes/shared-ui';
import { BaseWidgetData } from '../../models/dataview-dashboard.model';
import { DataExplorerSharedService } from '../../services/data-explorer-shared.service';
import { MatMenuTrigger } from '@angular/material/menu';

@Component({
    selector: 'sp-data-explorer-dashboard-widget',
    templateUrl: './data-explorer-dashboard-widget.component.html',
    styleUrls: ['./data-explorer-dashboard-widget.component.scss'],
})
export class DataExplorerDashboardWidgetComponent
    implements OnInit, OnDestroy, OnChanges
{
    @ViewChild('menuTrigger') menu: MatMenuTrigger;
    @ViewChild('timeSelectorMenu')
    timeSelectorMenu: TimeRangeSelectorMenuComponent;
    @Input()
    dashboardItem: DashboardItem;

    @Input()
    configuredWidget: DataExplorerWidgetModel;

    @Input()
    dataLakeMeasure: DataLakeMeasure;

    @Input()
    editMode: boolean;

    @Input()
    dataViewMode = false;

    @Input()
    gridsterItemComponent: GridsterItemComponent;

    @Input()
    previewMode = false;

    @Input()
    gridMode = true;

    @Input()
    widgetIndex: number;

    /**
     * This is the date range (start, end) to view the data and is set in data-explorer.ts
     */
    @Input()
    timeSettings: TimeSettings;

    @Input()
    globalTimeEnabled = true;

    @Output() deleteCallback: EventEmitter<number> = new EventEmitter<number>();
    @Output() startEditModeEmitter: EventEmitter<DataExplorerWidgetModel> =
        new EventEmitter<DataExplorerWidgetModel>();

    title = '';
    widgetLoaded = false;
    timerActive = false;
    loadingTime = 0;

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

    authSubscription: Subscription;
    widgetTypeChangedSubscription: Subscription;
    intervalSubscription: Subscription;

    errorMessage: SpLogMessage;

    componentRef: ComponentRef<BaseWidgetData<any>>;

    @ViewChild(WidgetDirective, { static: true }) widgetHost!: WidgetDirective;

    constructor(
        private widgetRegistryService: DataExplorerWidgetRegistry,
        private dashboardService: DataExplorerSharedService,
        private componentFactoryResolver: ComponentFactoryResolver,
        private widgetTypeService: WidgetTypeService,
        private authService: AuthService,
        private currentUserService: CurrentUserService,
        private timeSelectionService: TimeSelectionService,
    ) {}

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.widgetIndex && this.componentRef?.instance) {
            this.componentRef.instance.widgetIndex =
                changes.widgetIndex.currentValue;
        }
    }

    ngOnInit(): void {
        this.quickSelections ??=
            this.timeSelectionService.defaultQuickTimeSelections;
        this.labels ??= this.timeSelectionService.defaultLabels;
        this.authSubscription = this.currentUserService.user$.subscribe(
            user => {
                this.hasDataExplorerWritePrivileges = this.authService.hasRole(
                    UserPrivilege.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW,
                );
            },
        );
        this.widgetLoaded = true;
        this.title = this.dataLakeMeasure.measureName;
        this.widgetTypeChangedSubscription =
            this.widgetTypeService.widgetTypeChangeSubject.subscribe(
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
        this.componentRef?.destroy();
        this.authSubscription?.unsubscribe();
        this.widgetTypeChangedSubscription?.unsubscribe();
    }

    chooseWidget(widgetTypeId: string) {
        const widgetToDisplay =
            this.widgetRegistryService.getWidgetTemplate(widgetTypeId);
        this.loadComponent(widgetToDisplay.widgetComponent);
    }

    loadComponent(widgetToDisplay) {
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
        this.componentRef.instance.timeSettings = this.getTimeSettings();
        this.timeSelectionService.updateTimeSettings(
            this.quickSelections,
            this.getTimeSettings(),
            new Date(),
        );
        this.componentRef.instance.gridsterItem = this.dashboardItem;
        this.componentRef.instance.gridsterItemComponent =
            this.gridsterItemComponent;
        this.componentRef.instance.editMode = this.editMode;
        this.componentRef.instance.dataViewDashboardItem = this.dashboardItem;
        this.componentRef.instance.dataExplorerWidget = this.configuredWidget;
        this.componentRef.instance.previewMode = this.previewMode;
        this.componentRef.instance.gridMode = this.gridMode;
        this.componentRef.instance.widgetIndex = this.widgetIndex;
        const removeSub =
            this.componentRef.instance.removeWidgetCallback.subscribe(ev =>
                this.removeWidget(),
            );
        const timerSub = this.componentRef.instance.timerCallback.subscribe(
            ev => this.handleTimer(ev),
        );
        const errorSub = this.componentRef.instance.errorCallback.subscribe(
            ev => (this.errorMessage = ev),
        );

        this.componentRef.onDestroy(destroy => {
            this.componentRef.instance.cleanupSubscriptions();
            removeSub?.unsubscribe();
            timerSub?.unsubscribe();
            errorSub?.unsubscribe();
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
        this.intervalSubscription = interval(100)
            .pipe(takeWhile(() => this.timerActive))
            .subscribe(value => {
                this.loadingTime = (value * 100) / 1000;
            });
    }

    stopLoadingTimer() {
        this.timerActive = false;
        this.intervalSubscription.unsubscribe();
    }

    handleTimer(start: boolean) {
        start ? this.startLoadingTimer() : this.stopLoadingTimer();
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
}
