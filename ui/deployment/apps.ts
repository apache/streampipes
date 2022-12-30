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

import { App } from './apps.model';

export class AvailableAppsService {
    public static apps: App[] = [
        {
            appName: 'Asset Dashboards',
            appDescription:
                'Monitor measurements of your assets by placing visualizations on an image of your asset.',
            appId: 'asset-monitoring',
            appLink: 'asset-monitoring',
            appModuleLink: () =>
                import(
                    '../app-asset-monitoring/app-asset-monitoring.module'
                ).then(m => m.AppAssetMonitoringModule),
        },
    ];
}
