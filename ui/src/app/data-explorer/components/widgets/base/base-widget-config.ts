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
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    SimpleChanges,
} from '@angular/core';
import {
    DataExplorerWidgetModel,
    SourceConfig,
} from '@streampipes/platform-services';
import { WidgetConfigurationService } from '../../../services/widget-configuration.service';
import {
    DataExplorerVisConfig,
    FieldProvider,
} from '../../../models/dataview-dashboard.model';
import { DataExplorerFieldProviderService } from '../../../services/data-explorer-field-provider-service';
import { Subscription } from 'rxjs';

@Directive()
export abstract class BaseWidgetConfig<
        T extends DataExplorerWidgetModel,
        V extends DataExplorerVisConfig,
    >
    implements OnInit, OnChanges, OnDestroy
{
    @Input() currentlyConfiguredWidget: T;

    fieldProvider: FieldProvider;

    configChangedSubject: Subscription;

    constructor(
        protected widgetConfigurationService: WidgetConfigurationService,
        protected fieldService: DataExplorerFieldProviderService,
    ) {}

    ngOnInit(): void {
        this.makeFields();
        this.checkAndInitialize();
        this.configChangedSubject =
            this.widgetConfigurationService.configurationChangedSubject.subscribe(
                res => {
                    if (res.widgetId === this.currentlyConfiguredWidget._id) {
                        this.makeFields();
                        this.checkAndInitialize();
                    }
                },
            );
    }

    ngOnChanges(changes: SimpleChanges) {
        this.makeFields();
        if (changes.currentlyConfiguredWidget) {
            this.checkAndInitialize();
        }
    }

    checkAndInitialize() {
        if (!this.currentlyConfiguredWidget.visualizationConfig) {
            this.currentlyConfiguredWidget.visualizationConfig = {};
        }
        if (this.checkConfigurationValid()) {
            this.applyWidgetConfig(
                this.currentlyConfiguredWidget.visualizationConfig as V,
            );
        }
    }

    checkConfigurationValid() {
        this.currentlyConfiguredWidget.visualizationConfig.configurationValid =
            this.requiredFieldsForChartPresent();
        return this.currentlyConfiguredWidget.visualizationConfig
            .configurationValid;
    }

    makeFields() {
        const sourceConfigs: SourceConfig[] =
            this.currentlyConfiguredWidget.dataConfig.sourceConfigs;
        this.fieldProvider =
            this.fieldService.generateFieldLists(sourceConfigs);
    }

    triggerDataRefresh() {
        this.widgetConfigurationService.notify({
            widgetId: this.currentlyConfiguredWidget._id,
            refreshData: true,
            refreshView: true,
        });
    }

    triggerViewRefresh() {
        this.widgetConfigurationService.notify({
            widgetId: this.currentlyConfiguredWidget._id,
            refreshData: false,
            refreshView: true,
        });
    }

    protected abstract applyWidgetConfig(config: V): void;

    protected abstract requiredFieldsForChartPresent(): boolean;

    ngOnDestroy(): void {
        this.configChangedSubject?.unsubscribe();
    }
}
