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
import { FieldUpdateInfo } from '../models/field-update.model';

@Injectable({ providedIn: 'root' })
export class SpFieldUpdateService {
    public updateFieldSelection(
        fieldSelection: DataExplorerField[],
        fieldUpdateInfo: FieldUpdateInfo,
        filterFunction: (field: DataExplorerField) => boolean,
    ): DataExplorerField[] {
        const fields = fieldSelection.filter(
            field =>
                !fieldUpdateInfo.removedFields.find(
                    rm => rm.fullDbName === field.fullDbName,
                ),
        );
        fieldUpdateInfo.addedFields.forEach(field => {
            if (filterFunction(field)) {
                fields.push(field);
            }
        });
        return fields;
    }

    public updateSingleField(
        fieldSelection: DataExplorerField,
        availableFields: DataExplorerField[],
        fieldUpdateInfo: FieldUpdateInfo,
        filterFunction: (field: DataExplorerField) => boolean,
    ): DataExplorerField {
        let result = fieldSelection;
        if (
            fieldUpdateInfo.removedFields.find(
                rf => rf.fullDbName === fieldSelection.fullDbName,
            )
        ) {
            const existingFields = availableFields.concat(
                fieldUpdateInfo.addedFields,
            );
            if (existingFields.length > 0) {
                result = existingFields.find(field => filterFunction(field));
            }
        }

        return result;
    }

    updateNumericField(
        field: DataExplorerField,
        fieldUpdateInfo: FieldUpdateInfo,
    ): DataExplorerField {
        return this.updateSingleField(
            field,
            fieldUpdateInfo.fieldProvider.numericFields,
            fieldUpdateInfo,
            field => field.fieldCharacteristics.numeric,
        );
    }

    updateAnyField(
        field: DataExplorerField,
        fieldUpdateInfo: FieldUpdateInfo,
    ): DataExplorerField {
        return this.updateSingleField(
            field,
            fieldUpdateInfo.fieldProvider.allFields,
            fieldUpdateInfo,
            field => true,
        );
    }
}
