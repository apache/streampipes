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

import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { UnitDescription } from '../../../../model/UnitDescription';
import { RestService } from '../../../../services/rest.service';
import { UnitProviderService } from '../../../../services/unit-provider.service';
import { EventPropertyPrimitive } from '@streampipes/platform-services';

@Component({
    selector: 'sp-edit-unit-transformation',
    templateUrl: './edit-unit-transformation.component.html',
    styleUrls: ['./edit-unit-transformation.component.scss'],
})
export class EditUnitTransformationComponent implements OnInit {
    @Input() cachedProperty: EventPropertyPrimitive;
    @Input() originalProperty: EventPropertyPrimitive;

    @Input() isTimestampProperty: boolean;
    @Input() isNestedProperty: boolean;
    @Input() isListProperty: boolean;
    @Input() isPrimitiveProperty: boolean;
    @Input() isNumericProperty: boolean;

    @Input() showUnitTransformation: boolean;

    transformUnitEnable = false;
    possibleUnitTransformations: UnitDescription[] = [];
    selectUnit: UnitDescription;
    allUnits: UnitDescription[];
    stateCtrl = new UntypedFormControl();

    newUnitStateCtrl = new UntypedFormControl();
    filteredUnits: Observable<UnitDescription[]>;
    originalMeasurementUnitDisplay: string;

    constructor(
        private restService: RestService,
        private unitProviderService: UnitProviderService,
    ) {
        this.allUnits = this.unitProviderService
            .getUnits()
            .sort((a, b) => a.label.localeCompare(b.label));
        this.filteredUnits = this.stateCtrl.valueChanges.pipe(
            startWith(''),
            map(unit =>
                unit ? this._filteredUnits(unit) : this.allUnits.slice(),
            ),
        );
    }

    protected open = false;

    ngOnInit() {
        if (this.originalProperty) {
            if (this.originalProperty.measurementUnit !== undefined) {
                const unit = this.allUnits.find(
                    unitTmp =>
                        unitTmp.resource ===
                        this.originalProperty.measurementUnit,
                );
                this.originalMeasurementUnitDisplay = unit.label;
                this.transformUnitEnable = true;
                this.stateCtrl.setValue(unit.label);

                this.restService.getFittingUnits(unit).subscribe(result => {
                    this.possibleUnitTransformations = result;
                    if (
                        this.cachedProperty.measurementUnit !== undefined &&
                        this.cachedProperty.measurementUnit !==
                            this.originalProperty.measurementUnit
                    ) {
                        this.selectUnit = this.allUnits.find(
                            u =>
                                u.resource ===
                                this.cachedProperty.measurementUnit,
                        );
                        this.changeTargetUnit(this.selectUnit);
                    }
                });
            }
        }
    }

    compareFn(c1: any, c2: any): boolean {
        return c1 && c2 ? c1.resource === c2.resource : c1 === c2;
    }

    transformUnit() {
        if (this.transformUnitEnable) {
            this.transformUnitEnable = false;
            this.cachedProperty.measurementUnit =
                this.originalProperty.measurementUnit;
        } else {
            const unit = this.allUnits.find(
                unitTmp => unitTmp.label === this.stateCtrl.value,
            );
            if (!unit) {
                return;
            }

            this.restService.getFittingUnits(unit).subscribe(result => {
                this.possibleUnitTransformations = result;
                this.selectUnit = this.possibleUnitTransformations[0];
                this.transformUnitEnable = true;
                this.changeTargetUnit(this.selectUnit);
            });
        }
    }

    _filteredUnits(value: string): UnitDescription[] {
        const filterValue = value.toLowerCase();
        const units: UnitDescription[] = this.allUnits.filter(
            unit => unit.label.toLowerCase().indexOf(filterValue) === 0,
        );
        const unit: UnitDescription = this.allUnits.filter(
            unit => unit.label.toLocaleLowerCase() === filterValue,
        )[0];
        if (unit !== undefined) {
            this.cachedProperty.measurementUnit = unit.resource;
        } else {
            this.cachedProperty.measurementUnit = undefined;
        }
        return units;
    }

    changeTargetUnit(unit: UnitDescription) {
        this.cachedProperty.measurementUnit = unit.resource;
        this.newUnitStateCtrl.setValue(unit);
    }
}
