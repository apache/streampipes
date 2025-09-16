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
import { MatPaginatorIntl } from '@angular/material/paginator';

export function getCustomPaginatorIntl(): MatPaginatorIntl {
    const paginatorIntl = new MatPaginatorIntl();

    paginatorIntl.itemsPerPageLabel = 'Items per page:';
    paginatorIntl.nextPageLabel = 'Next';
    paginatorIntl.previousPageLabel = 'Previous';

    paginatorIntl.getRangeLabel = (
        page: number,
        pageSize: number,
        length: number,
    ) => {
        const start = page * pageSize + 1;
        const end = Math.min((page + 1) * pageSize, length);
        return `Showing documents ${start} - ${end}`;
    };

    return paginatorIntl;
}
