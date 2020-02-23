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

import {Input, OnChanges, SimpleChanges} from "@angular/core";
import {DashboardItem} from "../../../models/dashboard.model";
import {DashboardWidget} from "../../../../core-model/dashboard/DashboardWidget";
import {StaticPropertyExtractor} from "../../../sdk/extractor/static-property-extractor";
import {RxStompService} from "@stomp/ng2-stompjs";
import {Message} from "@stomp/stompjs";
import {Subscription} from "rxjs";
import {GridsterItem, GridsterItemComponent} from "angular-gridster2";
import {WidgetConfigBuilder} from "../../../registry/widget-config-builder";
import {VisualizablePipeline} from "../../../../core-model/dashboard/VisualizablePipeline";

export abstract class BaseStreamPipesWidget implements OnChanges {

    @Input() widget: DashboardItem;
    @Input() widgetConfig: DashboardWidget;
    @Input() widgetDataConfig: VisualizablePipeline;
    @Input() gridsterItem: GridsterItem;
    @Input() gridsterItemComponent: GridsterItemComponent;
    @Input() editMode: boolean;

    static readonly PADDING: number = 20;
    static readonly EDIT_HEADER_HEIGHT: number = 40;

    subscription: Subscription;

    hasSelectableColorSettings: boolean;
    hasTitlePanelSettings: boolean;

    selectedBackgroundColor: string;
    selectedPrimaryTextColor: string;
    selectedSecondaryTextColor: string;
    selectedTitle: string;

    defaultBackgroundColor: string = "#1B1464";
    defaultPrimaryTextColor: string = "#FFFFFF";
    defaultSecondaryTextColor: string = "#39B54A";

    protected constructor(private rxStompService: RxStompService) {
    }

    ngOnInit(): void {
        this.prepareConfigExtraction();
        this.subscription = this.rxStompService.watch("/topic/" +this.widgetDataConfig.topic).subscribe((message: Message) => {
            this.onEvent(JSON.parse(message.body));
        });
    }

    prepareConfigExtraction() {
        let extractor: StaticPropertyExtractor = new StaticPropertyExtractor(this.widgetDataConfig.schema, this.widgetConfig.dashboardWidgetSettings.config);
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

    protected abstract extractConfig(extractor: StaticPropertyExtractor);

    protected abstract onEvent(event: any);

    ngOnChanges(changes: SimpleChanges): void {
        if (changes["widgetConfig"]) {
            this.prepareConfigExtraction();
        }
    }
}