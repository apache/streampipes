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
import { FilterExpressionGroup } from '@streampipes/platform-services';

@Injectable({
    providedIn: 'root',
})
export class FilterExpressionPreviewService {
    format(expression?: FilterExpressionGroup): string {
        if (!expression) {
            return '';
        }

        return this.formatGroup(expression);
    }

    private formatGroup(group: FilterExpressionGroup): string {
        const children = group.children.map(child =>
            child.type === 'group'
                ? this.formatGroup(child)
                : this.formatCondition(
                      child.field,
                      child.operator,
                      child.condition,
                  ),
        );

        if (children.length === 0) {
            return '()';
        }

        return `(${children.join(` ${group.operator} `)})`;
    }

    private formatCondition(
        field: string,
        operator: string,
        condition: any,
    ): string {
        const displayValue =
            typeof condition === 'string' ? `${condition}` : String(condition);
        return `${field} ${operator} ${displayValue}`;
    }
}
