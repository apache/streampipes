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

import { CollectedSchemaRequirements } from '../sdk/collected-schema-requirements';
import { Datatypes } from '../sdk/model/datatypes';
import { Tuple2 } from '../../core-model/base/Tuple2';
import {
    ColorPickerStaticProperty,
    DashboardWidgetSettings,
    FreeTextStaticProperty,
    OneOfStaticProperty,
    Option,
    StaticProperty,
} from '@streampipes/platform-services';

export class WidgetConfigBuilder {
    static readonly BACKGROUND_COLOR_KEY: string = 'spi-background-color-key';
    static readonly PRIMARY_TEXT_COLOR_KEY: string =
        'spi-primary-text-color-key';
    static readonly SECONDARY_TEXT_COLOR_KEY: string =
        'spi-secondary-text-color-key';
    static readonly REFRESH_INTERVAL_KEY: string = 'spi-refresh-interval-key';
    static readonly QUERY_LIMIT_KEY: string = 'spi-query-limit-key';

    static readonly TITLE_KEY: string = 'spi-title-key';

    private widget: DashboardWidgetSettings;

    private constructor(
        widgetName: string,
        widgetLabel: string,
        withColors?: boolean,
        withTitlePanel?: boolean,
    ) {
        this.widget = new DashboardWidgetSettings();
        this.widget.widgetLabel = widgetLabel;
        this.widget.widgetName = widgetName;
        this.widget.config = [];
        if (withTitlePanel) {
            this.requiredTextParameter(
                WidgetConfigBuilder.TITLE_KEY,
                'Widget title',
                'The title of the widget',
            );
        }
        if (withColors) {
            this.requiredColorParameter(
                WidgetConfigBuilder.BACKGROUND_COLOR_KEY,
                'Background color',
                'The background' + ' color',
                '#1B1464',
            );
            this.requiredColorParameter(
                WidgetConfigBuilder.PRIMARY_TEXT_COLOR_KEY,
                'Primary text color',
                'The' + ' primary text color',
                '#FFFFFF',
            );
            this.requiredColorParameter(
                WidgetConfigBuilder.SECONDARY_TEXT_COLOR_KEY,
                'Secondary text color',
                'The' + ' secondary text' + ' color',
                '#39B54A',
            );
        }
        this.requiredIntegerParameter(
            WidgetConfigBuilder.REFRESH_INTERVAL_KEY,
            'Refresh interval [seconds]',
            'The interval in seconds in which new data should be fetched',
            5,
        );
    }

    static create(
        widgetName: string,
        widgetLabel: string,
    ): WidgetConfigBuilder {
        return new WidgetConfigBuilder(widgetName, widgetLabel);
    }

    static createWithSelectableColors(
        widgetName: string,
        widgetLabel: string,
    ): WidgetConfigBuilder {
        return new WidgetConfigBuilder(widgetName, widgetLabel, true);
    }

    static createWithSelectableColorsAndTitlePanel(
        widgetName: string,
        widgetLabel: string,
    ): WidgetConfigBuilder {
        return new WidgetConfigBuilder(widgetName, widgetLabel, true, true);
    }

    withDescription(description: string): WidgetConfigBuilder {
        this.widget.widgetDescription = description;
        return this;
    }

    withIcon(fontAwesomeIconName: string): WidgetConfigBuilder {
        this.widget.widgetIconName = fontAwesomeIconName;
        return this;
    }

    withNumberOfPastEvents(): WidgetConfigBuilder {
        const fst: FreeTextStaticProperty = this.prepareFreeTextStaticProperty(
            WidgetConfigBuilder.QUERY_LIMIT_KEY,
            'Past data',
            'The number of historic events that should be shown',
            Datatypes.Integer.toUri(),
        );
        fst.value = '50';
        this.widget.config.push(fst);

        return this;
    }

    requiredTextParameter(
        id: string,
        label: string,
        description: string,
    ): WidgetConfigBuilder {
        const fst: FreeTextStaticProperty = this.prepareFreeTextStaticProperty(
            id,
            label,
            description,
            Datatypes.String.toUri(),
        );
        this.widget.config.push(fst);
        return this;
    }

    requiredColorParameter(
        id: string,
        label: string,
        description: string,
        defaultColor?: string,
    ): WidgetConfigBuilder {
        const csp = new ColorPickerStaticProperty();
        csp['@class'] =
            'org.apache.streampipes.model.staticproperty.ColorPickerStaticProperty';
        csp.internalName = id;
        csp.label = label;
        csp.description = description;
        if (defaultColor) {
            csp.selectedColor = defaultColor;
        }
        this.widget.config.push(csp);
        return this;
    }

    requiredIntegerParameter(
        id: string,
        label: string,
        description: string,
        defaultValue?: number,
    ): WidgetConfigBuilder {
        const fst: FreeTextStaticProperty = this.prepareFreeTextStaticProperty(
            id,
            label,
            description,
            Datatypes.Integer.toUri(),
        );
        if (defaultValue) {
            fst.value = defaultValue.toString();
        }
        this.widget.config.push(fst);
        return this;
    }

    requiredSingleValueSelection(
        id: string,
        label: string,
        description: string,
        options: Tuple2<string, string>[],
    ): WidgetConfigBuilder {
        const osp: OneOfStaticProperty = new OneOfStaticProperty();
        this.prepareStaticProperty(
            id,
            label,
            description,
            osp,
            'org.apache.streampipes.model.staticproperty.OneOfStaticProperty',
        );

        osp.options = [];
        options.forEach(o => {
            const option = new Option();
            option['@class'] =
                'org.apache.streampipes.model.staticproperty.Option';
            option.name = o.a;
            option.internalName = o.b;
            osp.options.push(option);
        });
        this.widget.config.push(osp);
        return this;
    }

    requiredFloatParameter(
        id: string,
        label: string,
        description: string,
    ): WidgetConfigBuilder {
        const fst: FreeTextStaticProperty = this.prepareFreeTextStaticProperty(
            id,
            label,
            description,
            Datatypes.Float.toUri(),
        );
        this.widget.config.push(fst);
        return this;
    }

    requiredSchema(
        collectedSchemaRequirements: CollectedSchemaRequirements,
    ): WidgetConfigBuilder {
        this.widget.requiredSchema =
            collectedSchemaRequirements.getEventSchema();
        this.widget.config = this.widget.config.concat(
            collectedSchemaRequirements.getRequiredMappingProperties(),
        );

        return this;
    }

    prepareStaticProperty(
        id: string,
        label: string,
        description: string,
        sp: StaticProperty,
        targetClass: any,
    ) {
        sp.internalName = id;
        sp.label = label;
        sp.description = description;
        sp['@class'] = targetClass;
    }

    prepareFreeTextStaticProperty(
        id: string,
        label: string,
        description: string,
        datatype: string,
    ) {
        const fst: FreeTextStaticProperty = new FreeTextStaticProperty();
        this.prepareStaticProperty(
            id,
            label,
            description,
            fst,
            'org.apache.streampipes.model.staticproperty.FreeTextStaticProperty',
        );
        fst.requiredDatatype = datatype;

        return fst;
    }

    build(): DashboardWidgetSettings {
        for (let i = 0; i < this.widget.config.length; i++) {
            this.widget.config[i].index = i;
        }
        return this.widget;
    }
}
