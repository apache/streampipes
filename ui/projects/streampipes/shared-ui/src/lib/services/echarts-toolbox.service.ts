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

import { Injectable } from '@angular/core';
import { ToolboxFeatureOption } from 'echarts/types/src/component/toolbox/featureManager';

/**
 * This service provides default echarts toolbox features used in the data explorer.
 * Custom toolbox features can be registered by using the register method.
 */
@Injectable({ providedIn: 'root' })
export class SpEchartsToolboxService {
    toolboxItems: Record<string, ToolboxFeatureOption> = {
        dataView: { show: true },
        restore: { show: true },
        saveAsImage: { show: true },
    };

    customToolboxItems: Record<string, ToolboxFeatureOption> = {};

    getDefaultToolboxItems(): Record<string, ToolboxFeatureOption> {
        return this.toolboxItems;
    }

    getAllToolboxItems(): Record<string, ToolboxFeatureOption> {
        return {
            ...this.toolboxItems,
            ...this.customToolboxItems,
        };
    }

    getCustomToolboxItems(): Record<string, ToolboxFeatureOption> {
        return this.customToolboxItems;
    }

    registerToolboxItem(
        featureName: string,
        featureOptions: ToolboxFeatureOption,
    ) {
        this.customToolboxItems[featureName] = featureOptions;
    }
}
