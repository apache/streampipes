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

import { Directive, Input, OnChanges, SimpleChanges } from '@angular/core';
import { StaticPropertyExtractor } from '../../../sdk/extractor/static-property-extractor';
import { BehaviorSubject, interval, Observable, Subscription } from 'rxjs';
import { WidgetConfigBuilder } from '../../../registry/widget-config-builder';
import { ResizeService } from '../../../services/resize.service';
import { WidgetInfo } from '../../../models/gridster-info.model';
import {
  DashboardWidgetModel,
  DataLakeMeasure,
  DatalakeQueryParameterBuilder,
  DatalakeRestService,
  SpQueryResult
} from '@streampipes/platform-services';
import { map, switchMap } from 'rxjs/operators';

@Directive()
export abstract class BaseStreamPipesWidget implements OnChanges {

  protected constructor(protected dataLakeService: DatalakeRestService,
                        protected resizeService: ResizeService,
                        protected adjustPadding: boolean) {
  }

  static readonly PADDING: number = 20;
  static readonly EDIT_HEADER_HEIGHT: number = 40;
  static readonly TIMESTAMP_KEY = 'time';

  @Input() widgetConfig: DashboardWidgetModel;
  @Input() widgetDataConfig: DataLakeMeasure;
  @Input() itemWidth: number;
  @Input() itemHeight: number;
  @Input() editMode: boolean;

  subscription: Subscription;
  intervalSubject: BehaviorSubject<number>;

  hasSelectableColorSettings: boolean;
  hasTitlePanelSettings: boolean;

  selectedBackgroundColor: string;
  selectedPrimaryTextColor: string;
  selectedSecondaryTextColor: string;
  selectedTitle: string;

  defaultBackgroundColor = '#1B1464';
  defaultPrimaryTextColor = '#FFFFFF';
  defaultSecondaryTextColor = '#39B54A';

  refreshIntervalInSeconds = 5;
  queryLimit = 1;

  ngOnInit(): void {
    this.prepareConfigExtraction();
    this.resizeService.resizeSubject.subscribe(info => {
      this.onResize(info);
    });

    this.fireQuery().subscribe(result => this.processQueryResult(result));

    this.intervalSubject = new BehaviorSubject<number>(this.refreshIntervalInSeconds);
    this.subscription = this.intervalSubject.pipe(
      switchMap(val => interval(val * 1000)))
      .subscribe(() => {
        this.fireQuery().subscribe(result => {
          this.processQueryResult(result);
        });
      });
  }

  prepareConfigExtraction() {
    const extractor: StaticPropertyExtractor = new StaticPropertyExtractor(this.widgetDataConfig.eventSchema, this.widgetConfig.dashboardWidgetSettings.config);
    if (extractor.hasStaticProperty(WidgetConfigBuilder.BACKGROUND_COLOR_KEY)) {
      this.hasSelectableColorSettings = true;
      this.selectedBackgroundColor = extractor.selectedColor(WidgetConfigBuilder.BACKGROUND_COLOR_KEY);
      this.selectedPrimaryTextColor = extractor.selectedColor(WidgetConfigBuilder.PRIMARY_TEXT_COLOR_KEY);
      this.selectedSecondaryTextColor = extractor.selectedColor(WidgetConfigBuilder.SECONDARY_TEXT_COLOR_KEY);
    } else {
      this.selectedBackgroundColor = this.defaultBackgroundColor;
      this.selectedPrimaryTextColor = this.defaultPrimaryTextColor;
      this.selectedSecondaryTextColor = this.defaultSecondaryTextColor;
    }
    if (extractor.hasStaticProperty(WidgetConfigBuilder.TITLE_KEY)) {
      this.hasTitlePanelSettings = true;
      this.selectedTitle = extractor.stringParameter(WidgetConfigBuilder.TITLE_KEY);
    }
    if (extractor.hasStaticProperty(WidgetConfigBuilder.REFRESH_INTERVAL_KEY)) {
      this.refreshIntervalInSeconds = extractor.integerParameter(WidgetConfigBuilder.REFRESH_INTERVAL_KEY);
      if (this.intervalSubject) {
        this.intervalSubject.next(this.refreshIntervalInSeconds);
      }
    }
    this.queryLimit = this.getQueryLimit(extractor);
    this.extractConfig(extractor);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  computeCurrentWidth(width: number): number {
    return this.adjustPadding ?
      (width - (BaseStreamPipesWidget.PADDING * 2)) :
      width;
  }

  computeCurrentHeight(height: number): number {
    return this.adjustPadding ?
      (height - (BaseStreamPipesWidget.PADDING * 2) - this.editModeOffset() - this.titlePanelOffset()) :
      height - this.editModeOffset() - this.titlePanelOffset();
  }

  editModeOffset(): number {
    return this.editMode ? BaseStreamPipesWidget.EDIT_HEADER_HEIGHT : 0;
  }

  titlePanelOffset(): number {
    return this.hasTitlePanelSettings ? 20 : 0;
  }

  protected abstract extractConfig(extractor: StaticPropertyExtractor);

  protected abstract getQueryLimit(extractor: StaticPropertyExtractor): number;

  protected abstract onEvent(events: any[]);

  protected abstract onSizeChanged(width: number, height: number);

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['widgetConfig']) {
      this.prepareConfigExtraction();
    }
  }

  onResize(info: WidgetInfo) {
    if (info.id === this.widgetConfig._id) {
      setTimeout(() => {
        this.onSizeChanged(this.computeCurrentWidth(info.width),
          this.computeCurrentHeight(info.height));
      }, 100);
    }
  }

  fireQuery(): Observable<SpQueryResult> {
    return this.dataLakeService
      .getData(this.widgetDataConfig.measureName, this.buildQuery(), true)
      .pipe(map(res => res as SpQueryResult));
  }

  processQueryResult(queryResult: SpQueryResult) {
    if (queryResult.total > 0) {
      if (queryResult.allDataSeries.length === 1) {
        const series = queryResult.allDataSeries[0];
        const events = [];
        series.rows.forEach(row => {
          const event = {};
          series.headers.forEach((fieldName, index) => {
            event[fieldName] = row[index];
          });
          events.push(event);
        });
        this.onEvent(events.reverse());
      }
    }
  }

  buildQuery() {
    const queryBuilder = DatalakeQueryParameterBuilder.create();
    return queryBuilder.withLimit(this.queryLimit).withOrdering('DESC').build();
  }
}
