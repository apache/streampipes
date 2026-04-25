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

import { ClientDashboardItem } from '@streampipes/platform-services';

export type DashboardItemType =
    | 'chart'
    | 'rich-text'
    | 'header'
    | 'divider'
    | 'spacer';

export interface DashboardLayoutSettings {
    content?: string;
    size?: 'sm' | 'md' | 'lg';
    alignment?: 'left' | 'center' | 'right';
    label?: string;
    dividerStyle?: 'solid' | 'dashed';
}

export interface DashboardLayoutDefinition {
    type: Exclude<DashboardItemType, 'chart'>;
    icon: string;
    title: string;
    description: string;
    defaultName: string;
    defaultWidth: number;
    defaultHeight: number;
    defaultContent?: string;
}

const LAYOUT_COMPONENTS: DashboardItemType[] = [
    'rich-text',
    'header',
    'divider',
    'spacer',
];

export const DASHBOARD_LAYOUT_DEFINITIONS: DashboardLayoutDefinition[] = [
    {
        type: 'rich-text',
        icon: 'notes',
        title: 'Rich text',
        description: 'Add markdown notes, explanations, or instructions.',
        defaultName: 'Rich text',
        defaultWidth: 4,
        defaultHeight: 3,
        defaultContent:
            '## Notes\nUse this area to document the dashboard context.',
    },
    {
        type: 'header',
        icon: 'title',
        title: 'Header',
        description: 'Add a section headline to structure the dashboard.',
        defaultName: 'Header',
        defaultWidth: 12,
        defaultHeight: 1,
        defaultContent: 'Section heading',
    },
    {
        type: 'divider',
        icon: 'horizontal_rule',
        title: 'Divider',
        description: 'Separate dashboard sections with a visual line.',
        defaultName: 'Divider',
        defaultWidth: 12,
        defaultHeight: 1,
    },
    {
        type: 'spacer',
        icon: 'space_bar',
        title: 'Spacer',
        description: 'Reserve empty space in the layout.',
        defaultName: 'Spacer',
        defaultWidth: 2,
        defaultHeight: 1,
    },
];

export function getDashboardItemType(
    item: ClientDashboardItem | undefined,
): DashboardItemType {
    if (!item) {
        return 'chart';
    }

    const component = item.component as DashboardItemType | undefined;
    if (component && LAYOUT_COMPONENTS.includes(component)) {
        return component;
    }

    return 'chart';
}

export function isChartDashboardItem(
    item: ClientDashboardItem | undefined,
): boolean {
    return getDashboardItemType(item) === 'chart';
}

export function hasFixedHeightDashboardItem(
    item: ClientDashboardItem | undefined,
): boolean {
    return getDashboardItemType(item) === 'header';
}

export function getLayoutDefinition(
    type: Exclude<DashboardItemType, 'chart'>,
): DashboardLayoutDefinition {
    return (
        DASHBOARD_LAYOUT_DEFINITIONS.find(
            definition => definition.type === type,
        ) || DASHBOARD_LAYOUT_DEFINITIONS[0]
    );
}

export function getDashboardItemTypeLabel(type: DashboardItemType): string {
    if (type === 'chart') {
        return 'Chart';
    }

    return getLayoutDefinition(type).defaultName;
}

export function getDashboardItemTitle(
    item: ClientDashboardItem | undefined,
): string {
    if (!item) {
        return 'Chart';
    }

    const type = getDashboardItemType(item);
    if (type === 'chart') {
        return item.name || 'Chart';
    }

    const definition = getLayoutDefinition(type);
    const settings = parseDashboardLayoutSettings(item);
    const content = settings.content?.trim();

    if (type === 'rich-text' || type === 'header') {
        return content || item.name || definition.defaultName;
    }

    return item.name || definition.defaultName;
}

export function getDashboardItemLabel(item: ClientDashboardItem): string {
    return getDashboardItemTitle(item);
}

export function parseDashboardLayoutSettings(
    item: ClientDashboardItem | undefined,
): DashboardLayoutSettings {
    const rawSettings = item?.settings?.[0];
    if (!rawSettings) {
        return {};
    }

    try {
        const parsed = JSON.parse(rawSettings) as DashboardLayoutSettings;
        return parsed ?? {};
    } catch {
        return {};
    }
}

export function getDefaultDashboardLayoutSettings(
    type: Exclude<DashboardItemType, 'chart'>,
): DashboardLayoutSettings {
    switch (type) {
        case 'header':
            return {
                content: getLayoutDefinition(type).defaultContent,
                size: 'md',
                alignment: 'left',
            };
        case 'divider':
            return {
                label: '',
                dividerStyle: 'solid',
            };
        case 'spacer':
            return {};
        case 'rich-text':
        default:
            return {
                content: getLayoutDefinition(type).defaultContent,
            };
    }
}

export function getMergedDashboardLayoutSettings(
    item: ClientDashboardItem | undefined,
): DashboardLayoutSettings {
    const itemType = getDashboardItemType(item);
    if (itemType === 'chart') {
        return {};
    }

    return {
        ...getDefaultDashboardLayoutSettings(itemType),
        ...parseDashboardLayoutSettings(item),
    };
}

export function writeDashboardLayoutSettings(
    item: ClientDashboardItem,
    settings: DashboardLayoutSettings,
): void {
    const itemType = getDashboardItemType(item);
    if (itemType === 'chart') {
        return;
    }

    item.settings = [
        JSON.stringify({
            ...getDefaultDashboardLayoutSettings(itemType),
            ...settings,
        }),
    ];
}

export function applyDashboardItemGridConstraints(
    item: ClientDashboardItem,
): ClientDashboardItem {
    if (getDashboardItemType(item) === 'header') {
        item.rows = 1;
        item.h = 1;
        item.minH = 1;
        item.maxH = 1;
    }

    return item;
}
