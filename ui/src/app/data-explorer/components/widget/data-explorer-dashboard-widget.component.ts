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

import { Component, ComponentFactoryResolver, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { GridsterItemComponent } from 'angular-gridster2';
import {
  DashboardItem, DataExplorerDataConfig,
  DataExplorerWidgetModel,
  DataLakeMeasure,
  DataViewDataExplorerService,
  DateRange,
  TimeSettings
} from '@streampipes/platform-services';
import { DataDownloadDialogComponent } from '../../../core-ui/data-download-dialog/data-download-dialog.component';
import { interval } from 'rxjs';
import { takeWhile } from 'rxjs/operators';
import { DataExplorerWidgetRegistry } from '../../registry/data-explorer-widget-registry';
import { WidgetDirective } from './widget.directive';
import { BaseWidgetData } from '../widgets/base/data-explorer-widget-data';
import { WidgetTypeService } from '../../services/widget-type.service';
import { AuthService } from '../../../services/auth.service';
import { UserPrivilege } from '../../../_enums/user-privilege.enum';
import { DialogService, PanelType } from '@streampipes/shared-ui';

@Component({
  selector: 'sp-data-explorer-dashboard-widget',
  templateUrl: './data-explorer-dashboard-widget.component.html',
  styleUrls: ['./data-explorer-dashboard-widget.component.scss']
})
export class DataExplorerDashboardWidgetComponent implements OnInit {

  @Input()
  dashboardItem: DashboardItem;

  @Input()
  configuredWidget: DataExplorerWidgetModel;

  @Input()
  dataLakeMeasure: DataLakeMeasure;

  @Input()
  editMode: boolean;

  @Input()
  gridsterItemComponent: GridsterItemComponent;

  @Input()
  currentlyConfiguredWidgetId: string;

  @Input()
  previewMode = false;

  @Input()
  gridMode = true;

  /**
   * This is the date range (start, end) to view the data and is set in data-explorer.ts
   */
  @Input()
  timeSettings: TimeSettings;

  @Output() deleteCallback: EventEmitter<DataExplorerWidgetModel> = new EventEmitter<DataExplorerWidgetModel>();
  @Output() updateCallback: EventEmitter<DataExplorerWidgetModel> = new EventEmitter<DataExplorerWidgetModel>();
  @Output() configureWidgetCallback: EventEmitter<DataExplorerWidgetModel>
    = new EventEmitter<DataExplorerWidgetModel>();
  @Output() startEditModeEmitter: EventEmitter<DataExplorerWidgetModel> = new EventEmitter<DataExplorerWidgetModel>();

  title = '';
  widgetLoaded = false;

  msCounter = interval(10);
  timerActive = false;
  loadingTime = 0;

  hasDataExplorerWritePrivileges = false;
  hasDataExplorerDeletePrivileges = false;

  @ViewChild(WidgetDirective, {static: true}) widgetHost!: WidgetDirective;

  constructor(private dataViewDataExplorerService: DataViewDataExplorerService,
              private dialogService: DialogService,
              private componentFactoryResolver: ComponentFactoryResolver,
              private widgetTypeService: WidgetTypeService,
              private authService: AuthService) {
  }

  ngOnInit(): void {
    this.authService.user$.subscribe(user => {
      this.hasDataExplorerWritePrivileges = this.authService.hasRole(UserPrivilege.PRIVILEGE_WRITE_DATA_EXPLORER_VIEW);
      this.hasDataExplorerDeletePrivileges = this.authService.hasRole(UserPrivilege.PRIVILEGE_DELETE_DATA_EXPLORER_VIEW);
    });
    this.widgetLoaded = true;
    this.title = this.dataLakeMeasure.measureName;
    this.widgetTypeService.widgetTypeChangeSubject.subscribe(typeChange => {
      if (typeChange.widgetId === this.configuredWidget._id) {
        this.chooseWidget(typeChange.newWidgetTypeId);
      }
    });
    this.chooseWidget(this.configuredWidget.widgetType);
  }

  chooseWidget(widgetTypeId: string) {
    const widgets = DataExplorerWidgetRegistry.getAvailableWidgetTemplates();
    const widgetToDisplay = widgets.find(widget => widget.id === widgetTypeId);
    this.loadComponent(widgetToDisplay.componentClass);
  }

  loadComponent(widgetToDisplay) {
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory<BaseWidgetData<any>>(widgetToDisplay);

    const viewContainerRef = this.widgetHost.viewContainerRef;
    viewContainerRef.clear();

    const componentRef = viewContainerRef.createComponent<BaseWidgetData<any>>(componentFactory);
    componentRef.instance.dataExplorerWidget = this.configuredWidget;
    componentRef.instance.timeSettings = this.timeSettings;
    componentRef.instance.gridsterItem = this.dashboardItem;
    componentRef.instance.gridsterItemComponent = this.gridsterItemComponent;
    componentRef.instance.editMode = this.editMode;
    componentRef.instance.dataViewDashboardItem = this.dashboardItem;
    componentRef.instance.dataExplorerWidget = this.configuredWidget;
    componentRef.instance.previewMode = this.previewMode;
    componentRef.instance.gridMode = this.gridMode;
    const removeSub = componentRef.instance.removeWidgetCallback.subscribe(ev => this.removeWidget());
    const timerSub = componentRef.instance.timerCallback.subscribe(ev => this.handleTimer(ev));

    componentRef.onDestroy(destroy => {
      removeSub.unsubscribe();
      timerSub.unsubscribe();
    });
  }

  removeWidget() {
    this.deleteCallback.emit(this.configuredWidget);
  }

  downloadDataAsFile() {
    this.dialogService.open(DataDownloadDialogComponent, {
      panelType: PanelType.SLIDE_IN_PANEL,
      title: 'Download data',
      width: '50vw',
      data: {
        'date': DateRange.fromTimeSettings(this.timeSettings),
        'dataConfig': this.configuredWidget.dataConfig as DataExplorerDataConfig
      }
    });
  }

  startEditMode() {
    this.startEditModeEmitter.emit(this.configuredWidget);
  }

  triggerWidgetEditMode() {
    if (this.currentlyConfiguredWidgetId === this.configuredWidget._id) {
      this.configureWidgetCallback.emit();
    } else {
      this.configureWidgetCallback.emit(this.configuredWidget);
    }
  }

  startLoadingTimer() {
    this.timerActive = true;
    interval( 10 )
        .pipe(takeWhile(() => this.timerActive))
        .subscribe(value => {
      this.loadingTime = (value * 10 / 1000);
    });
  }

  stopLoadingTimer() {
    this.timerActive = false;
  }

  handleTimer(start: boolean) {
    start ? this.startLoadingTimer() : this.stopLoadingTimer();
  }
}
