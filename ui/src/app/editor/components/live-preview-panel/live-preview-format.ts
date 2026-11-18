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

import { PreviewEvent, PreviewField } from '../../services/live-preview.store';

const EMPTY_VALUE = '—';

const decimalFormat = new Intl.NumberFormat(undefined, {
    maximumFractionDigits: 3,
    useGrouping: false,
});

export function formatPreviewValue(value: unknown, timestamp = false): string {
    if (value === undefined || value === null) {
        return EMPTY_VALUE;
    }
    if (timestamp && typeof value === 'number') {
        return formatTimestamp(value);
    }
    if (typeof value === 'number') {
        return Number.isInteger(value)
            ? String(value)
            : decimalFormat.format(value);
    }
    if (typeof value === 'object') {
        return JSON.stringify(value);
    }
    return String(value);
}

export function formatTimestamp(epochMillis: number, now = Date.now()): string {
    const date = new Date(epochMillis);
    const time =
        date.toLocaleTimeString(undefined, { hour12: false }) +
        '.' +
        String(date.getMilliseconds()).padStart(3, '0');
    return date.toDateString() === new Date(now).toDateString()
        ? time
        : `${date.toLocaleDateString()} ${time}`;
}

/**
 * Field order for a table of events: schema fields first, in schema order, then fields that only
 * appear in the events (for example when the schema is not known).
 */
export function orderFields(
    schemaFields: PreviewField[],
    events: (PreviewEvent | undefined)[],
): PreviewField[] {
    const ordered = [...schemaFields];
    const known = new Set(ordered.map(f => f.runtimeName));
    const extra = new Set<string>();
    events
        .filter(e => e)
        .forEach(e =>
            Object.keys(e).forEach(k => !known.has(k) && extra.add(k)),
        );
    [...extra]
        .sort((a, b) => a.localeCompare(b))
        .forEach(runtimeName =>
            ordered.push({ runtimeName, timestamp: false }),
        );
    return ordered;
}
