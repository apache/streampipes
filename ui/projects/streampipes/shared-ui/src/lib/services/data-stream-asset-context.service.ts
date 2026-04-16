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

import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';
import { SpDataStream } from '@streampipes/platform-services';
import { SpAssetBrowserService } from '../components/asset-browser/asset-browser.service';
import { SpTableAssetContextService } from '../components/sp-table/sp-asset-context/sp-table-asset-context.service';
import { SpTableResolvedAssetContext } from '../components/sp-table/sp-table.model';

@Injectable({ providedIn: 'root' })
export class DataStreamAssetContextService {
    private assetBrowserService = inject(SpAssetBrowserService);
    private assetContextService = inject(SpTableAssetContextService);

    watchDataStreamAssetContext(
        dataStream?: SpDataStream,
    ): Observable<SpTableResolvedAssetContext | undefined> {
        return this.assetBrowserService.assetData$.pipe(
            map(assetData => {
                if (!assetData || !dataStream) {
                    return undefined;
                }

                const assetContextIndex =
                    this.assetContextService.buildAssetContextIndex(assetData);

                return (
                    assetContextIndex
                        .get('adapter')
                        ?.get(dataStream.correspondingAdapterId) ??
                    assetContextIndex.get('stream')?.get(dataStream.elementId)
                );
            }),
        );
    }
}
