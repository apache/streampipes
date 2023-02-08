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

export class ImageConfig extends WidgetConfig {
    static readonly TITLE_KEY: string = 'title-key';
    static readonly IMAGE_MAPPING_KEY: string = 'image-mapping';

    constructor() {
        super();
    }

    getConfig(): DashboardWidgetSettings {
        return WidgetConfigBuilder.create('image', 'Image')
            .withDescription('Displays an image (e.g., from a camera)')
            .withIcon('fas fa-image')
            .requiredSchema(
                SchemaRequirementsBuilder.create()
                    .requiredPropertyWithUnaryMapping(
                        ImageConfig.IMAGE_MAPPING_KEY,
                        'Select image field',
                        '',
                        EpRequirements.imageReq(),
                    )
                    .build(),
            )
            .requiredTextParameter(ImageConfig.TITLE_KEY, 'Title', 'The title')
            .build();
    }
}
