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

import { Input, OnChanges, SimpleChanges, Directive } from '@angular/core';
import { StaticPropertyExtractor } from '../../../sdk/extractor/static-property-extractor';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Message } from '@stomp/stompjs';
import { Subscription } from 'rxjs';
import { WidgetConfigBuilder } from '../../../registry/widget-config-builder';
import { ResizeService } from '../../../services/resize.service';
import { WidgetInfo } from '../../../models/gridster-info.model';
import { DashboardService } from '../../../services/dashboard.service';
import {
    DashboardWidgetModel,
    VisualizablePipeline
} from '../../../../../../projects/streampipes/platform-services/src/lib/model/gen/streampipes-model';

@Directive()
export abstract class BaseStreamPipesWidget implements OnChanges {


    protected constructor(private rxStompService: RxStompService,
                          protected dashboardService: DashboardService,
                          protected resizeService: ResizeService,
                          protected adjustPadding: boolean) {
    }

    static readonly PADDING: number = 20;
    static readonly EDIT_HEADER_HEIGHT: number = 40;

    // @Input() widget: DashboardItem;
    @Input() widgetConfig: DashboardWidgetModel;
    @Input() widgetDataConfig: VisualizablePipeline;
    @Input() itemWidth: number;
    @Input() itemHeight: number;
    // @Input() gridsterItem: GridsterItem;
    // @Input() gridsterItemComponent: GridsterItemComponent;
    @Input() editMode: boolean;

    subscription: Subscription;

    hasSelectableColorSettings: boolean;
    hasTitlePanelSettings: boolean;

    selectedBackgroundColor: string;
    selectedPrimaryTextColor: string;
    selectedSecondaryTextColor: string;
    selectedTitle: string;

    defaultBackgroundColor = '#1B1464';
    defaultPrimaryTextColor = '#FFFFFF';
    defaultSecondaryTextColor = '#39B54A';

    ngOnInit(): void {
        this.prepareConfigExtraction();
        this.resizeService.resizeSubject.subscribe(info => {
            this.onResize(info);
        });
        this.subscription = this.rxStompService.watch('/topic/' + this.widgetDataConfig.topic).subscribe((message: Message) => {
            this.onEvent(JSON.parse(message.body));
        });
    }

    prepareConfigExtraction() {
        const extractor: StaticPropertyExtractor = new StaticPropertyExtractor(this.widgetDataConfig.schema, this.widgetConfig.dashboardWidgetSettings.config);
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

    protected abstract onEvent(event: any);

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
}
