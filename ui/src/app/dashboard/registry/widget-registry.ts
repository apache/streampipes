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

import {NumberConfig} from "../components/widgets/number/number-config";
import {DashboardWidgetSettings} from "../../core-model/dashboard/DashboardWidgetSettings";
import {WidgetConfig} from "../components/widgets/base/base-config";
import {LineConfig} from "../components/widgets/line/line-config";
import {TableConfig} from "../components/widgets/table/table-config";
import {GaugeConfig} from "../components/widgets/gauge/gauge-config";
import {ImageConfig} from "../components/widgets/image/image-config";
import {AreaConfig} from "../components/widgets/area/area-config";
import {MapConfig} from "../components/widgets/map/map-config";
import {RawConfig} from "../components/widgets/raw/raw-config";
import {HtmlConfig} from "../components/widgets/html/html-config";
import {TrafficLightConfig} from "../components/widgets/trafficlight/traffic-light-config";

export class WidgetRegistry {

    private static availableWidgets: Array<WidgetConfig> = [
        new NumberConfig(),
        new LineConfig(),
        new TableConfig(),
        new GaugeConfig(),
        new ImageConfig(),
        new AreaConfig(),
        new MapConfig(),
        new RawConfig(),
        new HtmlConfig(),
        new TrafficLightConfig()
    ];

    static getAvailableWidgetTemplates(): Array<DashboardWidgetSettings> {
        let widgetTemplates = new Array<DashboardWidgetSettings>();
        this.availableWidgets.forEach(widget => widgetTemplates.push(widget.getConfig()));
        return widgetTemplates;
    }
}