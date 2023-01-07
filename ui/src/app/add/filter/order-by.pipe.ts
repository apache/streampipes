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

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'orderBy' })
export class OrderByPipe implements PipeTransform {
    transform(value: any[], order = '', column: string = ''): any[] {
        if (!value || order === '' || !order) {
            return value;
        }
        if (value.length <= 1) {
            return value;
        }
        if (!column || column === '') {
            if (order === 'asc') {
                return value.sort();
            } else {
                return value.sort().reverse();
            }
        }
        const sortedValues = value.sort((a, b) =>
            a[column].localeCompare(b[column]),
        );
        return order === 'asc' ? sortedValues : sortedValues.reverse();
    }
}
