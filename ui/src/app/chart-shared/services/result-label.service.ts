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
import { DataExplorerField, QueryConfig } from '@streampipes/platform-services';

@Injectable({ providedIn: 'root' })
export class ResultLabelService {
    makeFieldKey(field: Pick<DataExplorerField, 'fullDbName' | 'sourceIndex'>) {
        return `${field.fullDbName}:${field.sourceIndex}`;
    }

    getOverrides(queryConfig: QueryConfig): Record<string, string> {
        queryConfig.resultLabelOverrides ??= {};
        return queryConfig.resultLabelOverrides;
    }

    getOverride(
        queryConfig: QueryConfig,
        field: Pick<DataExplorerField, 'fullDbName' | 'sourceIndex'>,
    ): string | undefined {
        return this.getOverrides(queryConfig)[this.makeFieldKey(field)];
    }

    resolveLabel(
        queryConfig: QueryConfig,
        field: Pick<DataExplorerField, 'fullDbName' | 'sourceIndex'>,
        legacyLabel?: string,
    ): string {
        return (
            this.getOverride(queryConfig, field) ??
            legacyLabel ??
            field.fullDbName
        );
    }

    setOverride(
        queryConfig: QueryConfig,
        field: Pick<DataExplorerField, 'fullDbName' | 'sourceIndex'>,
        label: string,
        fallbackLabel?: string,
    ): void {
        const normalizedLabel = label.trim();
        const fallback = fallbackLabel ?? field.fullDbName;
        const overrides = this.getOverrides(queryConfig);
        const key = this.makeFieldKey(field);

        if (!normalizedLabel || normalizedLabel === fallback) {
            delete overrides[key];
            return;
        }

        overrides[key] = normalizedLabel;
    }
}
