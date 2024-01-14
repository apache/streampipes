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
import { DataExplorerField } from '@streampipes/platform-services';

@Injectable({ providedIn: 'root' })
export class SpFieldUpdateService {
    updateFieldSelection(
        fieldSelection: DataExplorerField[],
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
        filterFunction: (field: DataExplorerField) => boolean,
    ): DataExplorerField[] {
        const fields = fieldSelection.filter(
            field =>
                !removedFields.find(rm => rm.fullDbName === field.fullDbName),
        );
        addedFields.forEach(field => {
            if (filterFunction(field)) {
                fields.push(field);
            }
        });
        return fields;
    }

    updateSingleField(
        fieldSelection: DataExplorerField,
        availableFields: DataExplorerField[],
        addedFields: DataExplorerField[],
        removedFields: DataExplorerField[],
        filterFunction: (field: DataExplorerField) => boolean,
    ): DataExplorerField {
        let result = fieldSelection;
        if (
            removedFields.find(
                rf => rf.fullDbName === fieldSelection.fullDbName,
            )
        ) {
            const existingFields = availableFields.concat(addedFields);
            if (existingFields.length > 0) {
                result = existingFields.find(field => filterFunction(field));
            }
        }

        return result;
    }
}
