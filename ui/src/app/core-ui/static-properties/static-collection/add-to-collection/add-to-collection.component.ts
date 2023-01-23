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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { StaticPropertyUtilService } from '../../static-property-util.service';
import {
    FreeTextStaticProperty,
    OneOfStaticProperty,
    StaticProperty,
} from '@streampipes/platform-services';
import { Observable } from 'rxjs';

@Component({
    selector: 'sp-add-to-collection',
    templateUrl: './add-to-collection.component.html',
    styleUrls: ['./add-to-collection.component.css'],
})
export class AddToCollectionComponent {
    @Input()
    public staticPropertyTemplate: StaticProperty;

    @Output()
    addPropertyEmitter: EventEmitter<StaticProperty> =
        new EventEmitter<StaticProperty>();

    public showFileSelecion = false;

    public processingFile = false;

    public fileName: string;

    public hasError = false;
    public errorMessage = 'This is a test';

    constructor(private staticPropertyUtil: StaticPropertyUtilService) {}

    add() {
        const clone = this.staticPropertyUtil.clone(
            this.staticPropertyTemplate,
        );
        this.addPropertyEmitter.emit(clone);
    }

    selectFileSelection() {
        this.showFileSelecion = true;
    }

    closeFileSelection() {
        this.showFileSelecion = false;
        this.processingFile = false;
        this.hasError = false;
        this.fileName = '';
        this.errorMessage = '';
    }

    handleFileInput(target: any) {
        this.processingFile = true;

        const fileReader = new FileReader();
        this.fileName = target.files[0].name;

        fileReader.onload = e => {
            this.parseCsv(fileReader.result).subscribe(res => {
                res.pop();
                res.forEach((row, i) => {
                    const property: StaticProperty = this.getStaticProperty(
                        row,
                        i,
                    );
                    finalProperties.push(property);
                });

                if (!this.hasError) {
                    finalProperties.forEach(p => {
                        this.addPropertyEmitter.emit(p);
                    });
                    this.closeFileSelection();
                    this.fileName = '';
                }
            });
        };

        fileReader.readAsText(target.files[0]);

        // Parse file and return properties
        const finalProperties: StaticProperty[] = [];
    }

    private setError(errorMessage: string) {
        if (!this.hasError) {
            this.errorMessage = errorMessage;
            this.hasError = true;
        }
    }

    public parseCsv(str): Observable<any[]> {
        str = str.replace(/\r?\n|\r/g, '\n');
        const parseResult = new Observable<any[]>(observer => {
            const delimiter = ',';
            const headers = str.slice(0, str.indexOf('\n')).split(delimiter);

            const rows = str.slice(str.indexOf('\n') + 1).split('\n');

            const result = rows.map(row => {
                const values = row.split(delimiter);
                const el = headers.reduce((object, header, index) => {
                    object[header] = values[index];
                    return object;
                }, {});
                return el;
            });

            observer.next(result);
        });

        return parseResult;
    }

    public getStaticProperty(row: any, rowNumber: number): StaticProperty {
        const clone = this.staticPropertyUtil.clone(
            this.staticPropertyTemplate,
        );

        // Check that all values are within csv row
        clone.staticProperties.forEach(p => {
            if (p instanceof OneOfStaticProperty) {
                this.setOneOfStaticProperty(row, p, rowNumber);
            } else {
                this.setStaticProperty(row, p, rowNumber);
            }
        });

        return clone;
    }

    private setOneOfStaticProperty(
        row: any,
        property: OneOfStaticProperty,
        rowNumber: number,
    ) {
        const option = property.options.find(
            o => o.name === row[property.label],
        );
        if (option !== undefined) {
            option.selected = true;
        } else {
            this.setError(
                'Error in line ' +
                    rowNumber +
                    '. Value for "' +
                    property.label +
                    '" is not supported',
            );
        }
    }

    private setStaticProperty(
        row: any,
        property: FreeTextStaticProperty,
        rowNumber: number,
    ) {
        if (row[property.label] === undefined || row[property.label] === '') {
            this.setError(
                'Error in line ' +
                    rowNumber +
                    '. Value for "' +
                    property.label +
                    '" is not set',
            );
        } else {
            property.value = row[property.label];
        }
    }
}
