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
import { EpRequirements } from '../../../sdk/ep-requirements';
import { WidgetConfig } from '../base/base-config';
import { DashboardWidgetSettings } from '@streampipes/platform-services';

export class MapConfig extends WidgetConfig {
    static readonly LATITUDE_MAPPING_KEY: string = 'latitude-mapping';
    static readonly LONGITUDE_MAPPING_KEY: string = 'longitude-mapping';
    static readonly ITEMS_MAPPING_KEY: string = 'items-mapping';
    static readonly ID_MAPPING_KEY: string = 'ids-mapping';
    static readonly MARKER_TYPE_KEY: string = 'marker-type-mapping';
    static readonly CENTER_MAP_KEY: string = 'center-map-mapping';

    constructor() {
        super();
    }

    getConfig(): DashboardWidgetSettings {
        return WidgetConfigBuilder.createWithSelectableColorsAndTitlePanel(
            'map',
            'Map',
        )
            .withIcon('fas fa-map')
            .withDescription(
                'A map including a marker to show the live position of a thing',
            )
            .requiredSchema(
                SchemaRequirementsBuilder.create()
                    .requiredPropertyWithUnaryMapping(
                        MapConfig.LATITUDE_MAPPING_KEY,
                        'Latitude field',
                        '',
                        EpRequirements.latitudeReq(),
                    )
                    .requiredPropertyWithUnaryMapping(
                        MapConfig.LONGITUDE_MAPPING_KEY,
                        'Latitude field',
                        '',
                        EpRequirements.longitudeReq(),
                    )
                    .requiredPropertyWithNaryMapping(
                        MapConfig.ID_MAPPING_KEY,
                        'Group Markers',
                        'Each id gets its own marker',
                        EpRequirements.anyProperty(),
                    )
                    .requiredPropertyWithNaryMapping(
                        MapConfig.ITEMS_MAPPING_KEY,
                        'Fields to display',
                        '',
                        EpRequirements.anyProperty(),
                    )
                    .build(),
            )
            .requiredSingleValueSelection(
                MapConfig.CENTER_MAP_KEY,
                'Center map',
                'Center the map around new markers',
                [this.makeOption('Center'), this.makeOption('No Center')],
            )
            .requiredSingleValueSelection(
                MapConfig.MARKER_TYPE_KEY,
                'Marker type',
                '',
                [this.makeOption('Default'), this.makeOption('Car')],
            )
            .build();
    }
}
