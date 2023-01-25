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

import { WidgetConfig } from './base-config';
import { WidgetConfigBuilder } from '../../../registry/widget-config-builder';
import { SchemaRequirementsBuilder } from '../../../sdk/schema-requirements-builder';
import { EpRequirements } from '../../../sdk/ep-requirements';
import { DashboardWidgetSettings } from '@streampipes/platform-services';

export abstract class BaseNgxLineConfig extends WidgetConfig {
    static readonly NUMBER_MAPPING_KEY: string = 'number-mapping';
    static readonly TIMESTAMP_MAPPING_KEY: string = 'timestamp-mapping';
    static readonly MIN_Y_AXIS_KEY: string = 'min-y-axis-key';
    static readonly MAX_Y_AXIS_KEY: string = 'max-y-axis-key';

    getConfig(): DashboardWidgetSettings {
        return WidgetConfigBuilder.createWithSelectableColorsAndTitlePanel(
            this.getWidgetName(),
            this.getWidgetLabel(),
        )
            .withDescription(this.getWidgetDescription())
            .withIcon(this.getWidgetIcon())
            .withNumberOfPastEvents()
            .requiredSchema(
                SchemaRequirementsBuilder.create()
                    .requiredPropertyWithUnaryMapping(
                        BaseNgxLineConfig.NUMBER_MAPPING_KEY,
                        'Number field',
                        '',
                        EpRequirements.numberReq(),
                    )
                    .build(),
            )
            .requiredIntegerParameter(
                BaseNgxLineConfig.MIN_Y_AXIS_KEY,
                'Y-axis range (min)',
                '',
            )
            .requiredIntegerParameter(
                BaseNgxLineConfig.MAX_Y_AXIS_KEY,
                'Y-axis range (max)',
                '',
            )
            .build();
    }

    abstract getWidgetName(): string;

    abstract getWidgetLabel(): string;

    abstract getWidgetDescription(): string;

    abstract getWidgetIcon(): string;
}
