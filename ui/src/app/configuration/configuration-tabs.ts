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

import { SpNavigationItem } from '@streampipes/shared-ui';

export class SpConfigurationTabs {
    public static getTabs(): SpNavigationItem[] {
        return [
            {
                itemId: 'general',
                itemTitle: 'General',
                itemLink: ['configuration', 'general'],
            },
            {
                itemId: 'datalake',
                itemTitle: 'Data Lake',
                itemLink: ['configuration', 'datalake'],
            },
            {
                itemId: 'email',
                itemTitle: 'Mail',
                itemLink: ['configuration', 'email'],
            },
            {
                itemId: 'export',
                itemTitle: 'Export/Import',
                itemLink: ['configuration', 'export'],
            },
            {
                itemId: 'messaging',
                itemTitle: 'Messaging',
                itemLink: ['configuration', 'messaging'],
            },
            {
                itemId: 'pipelineelement',
                itemTitle: 'Pipeline Element Configuration',
                itemLink: ['configuration', 'pipelineelement'],
            },
            {
                itemId: 'security',
                itemTitle: 'Security',
                itemLink: ['configuration', 'security'],
            },
        ];
    }
}
