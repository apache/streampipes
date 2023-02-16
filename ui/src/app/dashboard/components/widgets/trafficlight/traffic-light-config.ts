/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import { WidgetConfigBuilder } from '../../../registry/widget-config-builder';
import { SchemaRequirementsBuilder } from '../../../sdk/schema-requirements-builder';
import { WidgetConfig } from '../base/base-config';
import { EpRequirements } from '../../../sdk/ep-requirements';
import { DashboardWidgetSettings } from '@streampipes/platform-services';

export class TrafficLightConfig extends WidgetConfig {
    static readonly NUMBER_MAPPING_KEY = 'number-field';
    static readonly CRITICAL_VALUE_KEY = 'critical-value-key';
    static readonly CRITICAL_VALUE_LIMIT = 'critical-value-limit';
    static readonly WARNING_RANGE_KEY = 'warning-range';

    constructor() {
        super();
    }

    getConfig(): DashboardWidgetSettings {
        return WidgetConfigBuilder.createWithSelectableColorsAndTitlePanel(
            'trafficlight',
            'Traffic Light',
        )
            .withIcon('fas fa-traffic-light')
            .withDescription(
                'A traffic light visualization with customizable warning range and threshold',
            )
            .requiredSchema(
                SchemaRequirementsBuilder.create()
                    .requiredPropertyWithUnaryMapping(
                        TrafficLightConfig.NUMBER_MAPPING_KEY,
                        'Field to observe',
                        '',
                        EpRequirements.numberReq(),
                    )
                    .build(),
            )
            .requiredIntegerParameter(
                TrafficLightConfig.CRITICAL_VALUE_KEY,
                'Threshold',
                '',
            )
            .requiredSingleValueSelection(
                TrafficLightConfig.CRITICAL_VALUE_LIMIT,
                'Operator',
                '',
                [
                    this.makeOption('Upper' + ' Limit'),
                    this.makeOption('Under Limit'),
                ],
            )
            .requiredIntegerParameter(
                TrafficLightConfig.WARNING_RANGE_KEY,
                'Warning range (percent)',
                '',
            )
            .build();
    }
}
