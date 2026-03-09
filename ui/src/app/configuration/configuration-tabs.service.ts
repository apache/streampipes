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

import {
    SpConfigurationSectionsService,
    SpNavigationItem,
} from '@streampipes/shared-ui';
import { Injectable, Type, inject } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class SpConfigurationTabsService {
    private configurationSectionsService = inject(
        SpConfigurationSectionsService,
    );

    public getTabs(): SpNavigationItem[] {
        return this.configurationSectionsService.getTabs();
    }

    public getTabTitle(itemId: string): string {
        return this.configurationSectionsService.getTabTitle(itemId);
    }

    public getSectionComponent(
        itemId: string,
    ): Promise<Type<unknown> | undefined> {
        return this.configurationSectionsService.getSectionComponent(itemId);
    }

    public getDefaultTab(): SpNavigationItem | undefined {
        return this.configurationSectionsService.getDefaultTab();
    }

    public isTabActive(
        activeTabs: SpNavigationItem[],
        itemId: string,
    ): boolean {
        return this.configurationSectionsService.isTabActive(
            activeTabs,
            itemId,
        );
    }
}
