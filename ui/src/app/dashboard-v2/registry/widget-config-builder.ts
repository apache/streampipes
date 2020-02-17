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

import {FreeTextStaticProperty} from "../../connect/model/FreeTextStaticProperty";
import {CollectedSchemaRequirements} from "../sdk/collected-schema-requirements";
import {DashboardWidgetSettings} from "../../core-model/dashboard/DashboardWidgetSettings";
import {Datatypes} from "../sdk/model/datatypes";
import {ColorPickerStaticProperty} from "../../connect/model/ColorPickerStaticProperty";

export class WidgetConfigBuilder {

    static readonly BACKGROUND_COLOR_KEY: string = "spi-background-color-key";
    static readonly PRIMARY_TEXT_COLOR_KEY: string = "spi-primary-text-color-key";
    static readonly SECONDARY_TEXT_COLOR_KEY: string = "spi-secondary-text-color-key";

    static readonly TITLE_KEY: string = "spi-title-key";

    private widget: DashboardWidgetSettings;

    private constructor(widgetName: string, widgetLabel: string, withColors?: boolean, withTitlePanel?: boolean) {
        this.widget = new DashboardWidgetSettings();
        this.widget.widgetLabel = widgetLabel;
        this.widget.widgetName = widgetName;
        this.widget.config = [];
        if (withColors) {
        this.requiredColorParameter(WidgetConfigBuilder.BACKGROUND_COLOR_KEY, "Background color", "The background" +
            " color", "#000000");
        this.requiredColorParameter(WidgetConfigBuilder.PRIMARY_TEXT_COLOR_KEY, "Primary text color", "The" +
                " primary text color", "#707070");
        this.requiredColorParameter(WidgetConfigBuilder.SECONDARY_TEXT_COLOR_KEY, "Secondary text color", "The" +
            " secondary text" +
            " color", "#bebebe")
        }
        if (withTitlePanel) {
        this.requiredTextParameter(WidgetConfigBuilder.TITLE_KEY, "Title", "The title")
        }
    }

    static create(widgetName: string, widgetLabel: string): WidgetConfigBuilder {
        return new WidgetConfigBuilder(widgetName, widgetLabel);
    }

    static createWithSelectableColors(widgetName: string, widgetLabel: string): WidgetConfigBuilder {
        return new WidgetConfigBuilder(widgetName, widgetLabel, true);
    }

    static createWithSelectableColorsAndTitlePanel(widgetName: string, widgetLabel: string): WidgetConfigBuilder {
        return new WidgetConfigBuilder(widgetName, widgetLabel, true, true);
    }

    requiredTextParameter(id: string, label: string, description: string): WidgetConfigBuilder {
        let fst: FreeTextStaticProperty = this.prepareStaticProperty(id, label, description, Datatypes.String.toUri())
        this.widget.config.push(fst);
        return this;
    }

    requiredColorParameter(id: string, label: string, description: string, defaultColor?: string): WidgetConfigBuilder {
        let csp = new ColorPickerStaticProperty();
        csp.internalName = id;
        csp.label = label;
        csp.description = description;
        if (defaultColor) {
            csp.selectedColor = defaultColor;
        }
        this.widget.config.push(csp);
        return this;
    }


    requiredIntegerParameter(id: string, label: string, description: string): WidgetConfigBuilder {
        let fst: FreeTextStaticProperty = this.prepareStaticProperty(id, label, description, Datatypes.Integer.toUri())
        this.widget.config.push(fst);
        return this;
    }

    requiredFloatParameter(id: string, label: string, description: string): WidgetConfigBuilder {
        let fst: FreeTextStaticProperty = this.prepareStaticProperty(id, label, description, Datatypes.Float.toUri())
        this.widget.config.push(fst);
        return this;
    }

    requiredSchema(collectedSchemaRequirements: CollectedSchemaRequirements): WidgetConfigBuilder {
        this.widget.requiredSchema = collectedSchemaRequirements.getEventSchema();
        this.widget.config = this.widget.config.concat(collectedSchemaRequirements.getRequiredMappingProperties());

        return this;
    }

    prepareStaticProperty(id: string, label: string, description: string, datatype: string) {
        let fst: FreeTextStaticProperty = new FreeTextStaticProperty();
        fst.internalName = id;
        fst.label = label;
        fst.description = description;
        fst.requiredDatatype = datatype;

        return fst;
    }

    build(): DashboardWidgetSettings {
        return this.widget;
    }

}