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

import { WidgetConfig } from '../base/base-config';
import { WidgetConfigBuilder } from '../../../registry/widget-config-builder';
import { SchemaRequirementsBuilder } from '../../../sdk/schema-requirements-builder';
import { EpRequirements } from '../../../sdk/ep-requirements';
import { DashboardWidgetSettings } from '@streampipes/platform-services';

export class GaugeConfig extends WidgetConfig {
    static readonly TITLE_KEY: string = 'title-key';
    static readonly NUMBER_MAPPING_KEY: string = 'number-mapping';
    static readonly COLOR_KEY: string = 'color-key';
    static readonly MIN_KEY: string = 'min-key';
    static readonly MAX_KEY: string = 'max-key';

    constructor() {
        super();
    }

    getConfig(): DashboardWidgetSettings {
        return WidgetConfigBuilder.createWithSelectableColorsAndTitlePanel(
            'gauge',
            'Gauge',
        )
            .withDescription('A gauge visualization')
            .withIcon('fas fa-tachometer-alt')
            .requiredSchema(
                SchemaRequirementsBuilder.create()
                    .requiredPropertyWithUnaryMapping(
                        GaugeConfig.NUMBER_MAPPING_KEY,
                        'Select property',
                        '',
                        EpRequirements.numberReq(),
                    )
                    .build(),
            )
            .requiredIntegerParameter(
                GaugeConfig.MIN_KEY,
                'Min Y axis value',
                '',
            )
            .requiredIntegerParameter(
                GaugeConfig.MAX_KEY,
                'Max Y axis value',
                '',
            )
            .build();
    }
}
